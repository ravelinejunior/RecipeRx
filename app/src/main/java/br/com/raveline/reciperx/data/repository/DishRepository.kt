package br.com.raveline.reciperx.data.repository

import androidx.annotation.WorkerThread
import br.com.raveline.reciperx.data.database.dao.DishDao
import br.com.raveline.reciperx.data.model.DishModel
import kotlinx.coroutines.flow.Flow

class DishRepository(private val dishDao: DishDao) {

    val allDishes = dishDao.selectAllDishes()

    val favoriteDishes = dishDao.selectFavoritesDishes()

    @WorkerThread
    suspend fun insertDish(dishModel: DishModel){
        dishDao.insertDish(dishModel)
    }

    @WorkerThread
    suspend fun updateDish(dishModel: DishModel){
        dishDao.updateDish(dishModel)
    }

    @WorkerThread
    suspend fun deleteDish(dishModel: DishModel){
        dishDao.deleteFavoriteDish(dishModel)
    }

    @WorkerThread
    suspend fun deleteAllDishes(){
        dishDao.deleteAllDishes()
    }

    @WorkerThread
    fun getDishesByFilter(value:String) : Flow<List<DishModel>> = dishDao.selectDishByFilter(value)

}