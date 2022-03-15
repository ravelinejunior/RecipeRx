package br.com.raveline.reciperx.viewmodel

import android.content.Context
import androidx.lifecycle.*
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.data.model.RecipeModel
import br.com.raveline.reciperx.data.model.Recipes
import br.com.raveline.reciperx.data.remote.RecipesApiService
import br.com.raveline.reciperx.data.repository.DataStoreRepository
import br.com.raveline.reciperx.data.repository.DishRepository
import br.com.raveline.reciperx.utils.SystemFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RandomViewModel @Inject constructor(
    private val repository: DishRepository,
    @ApplicationContext val context: Context,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val randomRecipesApiServices = RecipesApiService()

    private val compositeDisposable = CompositeDisposable()

    private val mutableRecipeResponse = MutableLiveData<Recipes>()
    val recipeRecipesLiveData: LiveData<Recipes> get() = mutableRecipeResponse

    private val _uiStateFlow = MutableStateFlow<UiState>(UiState.Initial)
    val uiStateFlow: StateFlow<UiState> get() = _uiStateFlow

    val allRecipes: LiveData<List<RecipeModel>> = repository.allRecipes.asLiveData()

    var isConnected = MutableLiveData(false)
    var backOnline = MutableLiveData(false)

    val hasRecipeSaved = dataStoreRepository.hasRecipesSaved.asLiveData().value

    fun getRecipes(isSwipe: Boolean, tag: String = "main course") {
        _uiStateFlow.value = UiState.Loading

        try {
            viewModelScope.launch {
                if (allRecipes.value.isNullOrEmpty() || isSwipe) {
                    getRandomRecipes(tag)
                }

                _uiStateFlow.value = UiState.Success
            }

        } catch (e: Exception) {
            _uiStateFlow.value = UiState.Error
            e.printStackTrace()
        }
    }


    private fun getRandomRecipes(tag: String) {

        _uiStateFlow.value = UiState.Loading

        if (SystemFunctions.isNetworkAvailable(context)) {

            isConnected.value = true
            backOnline.value = true

            compositeDisposable.add(
                randomRecipesApiServices.getRandomRecipes(tag)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Recipes>() {
                        override fun onSuccess(t: Recipes) {

                            viewModelScope.launch(Main) {
                                repository.deleteAllRecipes()
                                repository.insertRecipes(t.recipeModels)
                                dataStoreRepository.saveRecipesInStore(true)
                                _uiStateFlow.value = UiState.Success
                                mutableRecipeResponse.value = (t)
                            }
                        }

                        override fun onError(e: Throwable) {
                            _uiStateFlow.value = UiState.Error
                            e.printStackTrace()
                        }
                    })
            )
        } else {
            _uiStateFlow.value = UiState.NoConnection
            isConnected.value = false
            backOnline.value = false
        }

    }

    suspend fun saveSelectedOnDatabase(dish: DishModel) {
        repository.insertDish(dish)
    }

    sealed class UiState {
        object Success : UiState()
        object Error : UiState()
        object NoConnection : UiState()
        object Loading : UiState()
        object Initial : UiState()
    }
}