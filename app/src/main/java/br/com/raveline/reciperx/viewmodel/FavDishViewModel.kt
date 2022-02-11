package br.com.raveline.reciperx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.data.repository.DishRepository
import kotlinx.coroutines.launch

class FavDishViewModel(private val repository: DishRepository):ViewModel() {

    fun insert(dishModel: DishModel) = viewModelScope.launch {
        repository.insertDish(dishModel)
    }

    suspend fun getDishes():List<DishModel> = repository.getDishes()

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