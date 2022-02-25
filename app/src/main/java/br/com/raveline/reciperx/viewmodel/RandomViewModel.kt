package br.com.raveline.reciperx.viewmodel

import androidx.lifecycle.*
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.data.model.RecipeModel
import br.com.raveline.reciperx.data.model.Recipes
import br.com.raveline.reciperx.data.remote.RecipesApiService
import br.com.raveline.reciperx.data.repository.DishRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RandomViewModel(private val repository: DishRepository) : ViewModel() {
    private val randomRecipesApiServices = RecipesApiService()

    private val compositeDisposable = CompositeDisposable()

    private val mutableRecipeResponse = MutableLiveData<Recipes>()
    val recipeRecipesLiveData: LiveData<Recipes> get() = mutableRecipeResponse

    private val _uiStateFlow = MutableStateFlow<UiState>(UiState.Initial)
    val uiStateFlow: StateFlow<UiState> get() = _uiStateFlow

    val allRecipes: LiveData<List<RecipeModel>> = repository.allRecipes.asLiveData()

    fun getOfflineRecipes() {
        _uiStateFlow.value = UiState.Loading

        try {
            if (allRecipes.value.isNullOrEmpty()) {
                getRandomRecipes()
            }
            _uiStateFlow.value = UiState.Success

        } catch (e: Exception) {
            _uiStateFlow.value = UiState.Error
            e.printStackTrace()

        }
    }


    private fun getRandomRecipes() {

        _uiStateFlow.value = UiState.Loading

        compositeDisposable.add(
            randomRecipesApiServices.getRandomRecipes()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Recipes>() {
                    override fun onSuccess(t: Recipes) {

                        viewModelScope.launch(Main) {
                            _uiStateFlow.value = UiState.Success
                            mutableRecipeResponse.postValue(t)
                            repository.deleteAllRecipes()
                            repository.insertRecipes(t.recipeModels)
                        }
                    }

                    override fun onError(e: Throwable) {
                        _uiStateFlow.value = UiState.Error
                        e.printStackTrace()
                    }
                })
        )

    }

    suspend fun saveSelectedOnDatabase(dish: DishModel) {
        repository.insertDish(dish)
    }

    sealed class UiState {
        object Success : UiState()
        object Error : UiState()
        object Loading : UiState()
        object Initial : UiState()
    }
}