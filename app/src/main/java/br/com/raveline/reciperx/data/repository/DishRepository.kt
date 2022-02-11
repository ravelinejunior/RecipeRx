package br.com.raveline.reciperx.data.repository

import androidx.annotation.WorkerThread
import br.com.raveline.reciperx.data.database.dao.DishDao
import br.com.raveline.reciperx.data.model.DishModel

class DishRepository(private val dishDao: DishDao) {


    @WorkerThread
    suspend fun insertDish(dishModel: DishModel){
        dishDao.insertDish(dishModel)
    }

    @WorkerThread
    suspend fun getDishes():List<DishModel>{
        return dishDao.selectAllDishes()
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

}