package br.com.raveline.reciperx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.data.repository.DishRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavDishViewModel(private val repository: DishRepository):ViewModel() {

    val allDish = repository.allDishes.asLiveData()

    fun insert(dishModel: DishModel) = viewModelScope.launch {
        repository.insertDish(dishModel)
    }

    fun updateDish(dishModel: DishModel) = viewModelScope.launch {
        repository.updateDish(dishModel)
    }

    fun deleteDish(dishModel: DishModel) = viewModelScope.launch {
        repository.deleteDish(dishModel)
    }

    fun deleteDishes() = viewModelScope.launch {
        repository.deleteAllDishes()
    }



}