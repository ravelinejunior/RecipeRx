package br.com.raveline.reciperx.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.raveline.reciperx.data.repository.DishRepository
import br.com.raveline.reciperx.viewmodel.FavDishViewModel
import br.com.raveline.reciperx.viewmodel.RandomViewModel

class RandomViewModelFactory(
    private val repository: DishRepository
):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RandomViewModel::class.java)){
            return RandomViewModel(repository) as T
        }
        throw IllegalArgumentException("Class doesn't match!")
    }

}