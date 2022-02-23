package br.com.raveline.reciperx.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.raveline.reciperx.data.model.Recipes
import br.com.raveline.reciperx.data.remote.RecipesApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RandomViewModel : ViewModel() {
    private val randomRecipesApiServices = RecipesApiService()

    private val compositeDisposable = CompositeDisposable()

    private val mutableLoadRandomDish = MutableLiveData<Boolean>()
    val loadLiveData: LiveData<Boolean> get() = mutableLoadRandomDish

    private val mutableRecipeResponse = MutableLiveData<Recipes>()
    val recipeRecipesLiveData: LiveData<Recipes> get() = mutableRecipeResponse

    private val _uiStateFlow = MutableStateFlow<UiState>(UiState.Initial)
    val uiStateFlow: StateFlow<UiState> get() = _uiStateFlow

     fun getRandomRecipes() {

        _uiStateFlow.value = UiState.Loading

        compositeDisposable.add(
            randomRecipesApiServices.getRandomRecipes()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Recipes>() {
                    override fun onSuccess(t: Recipes) {
                        _uiStateFlow.value = UiState.Success
                        mutableRecipeResponse.postValue(t)
                    }

                    override fun onError(e: Throwable) {
                        _uiStateFlow.value = UiState.Error
                        e.printStackTrace()
                    }
                })
        )
    }


    sealed class UiState() {
        object Success : UiState()
        object Error : UiState()
        object Loading : UiState()
        object Initial : UiState()
    }
}