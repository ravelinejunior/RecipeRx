package br.com.raveline.reciperx.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.raveline.reciperx.data.repository.DishRepository
import br.com.raveline.reciperx.viewmodel.FavDishViewModel

class FavDishViewModelFactory(
    private val repository: DishRepository
):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Class doesn't match!")
    }

}