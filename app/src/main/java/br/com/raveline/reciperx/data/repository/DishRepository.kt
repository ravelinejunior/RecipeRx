package br.com.raveline.reciperx.data.repository

import androidx.annotation.WorkerThread
import br.com.raveline.reciperx.data.database.dao.DishDao
import br.com.raveline.reciperx.data.database.dao.RecipeDao
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.data.model.RecipeModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DishRepository @Inject constructor(
    private val dishDao: DishDao,
    private val recipeDao: RecipeDao
) {

    val allDishes = dishDao.selectAllDishes()

    val favoriteDishes = dishDao.selectFavoritesDishes()

    val allRecipes = recipeDao.selectAllFromRecipes()

    @WorkerThread
    suspend fun insertDish(dishModel: DishModel) {
        dishDao.insertDish(dishModel)
    }

    @WorkerThread
    suspend fun insertRecipes(recipes: List<RecipeModel>) {
        recipeDao.insertRecipes(recipes)
    }

    @WorkerThread
    suspend fun deleteAllRecipes() {
        recipeDao.deleteAllRecipes()
    }

    @WorkerThread
    suspend fun updateDish(dishModel: DishModel) {
        dishDao.updateDish(dishModel)
    }

    @WorkerThread
    suspend fun deleteDish(dishModel: DishModel) {
        dishDao.deleteFavoriteDish(dishModel)
    }

    @WorkerThread
    suspend fun deleteAllDishes() {
        dishDao.deleteAllDishes()
    }

    @WorkerThread
    fun getDishesByFilter(value: String): Flow<List<DishModel>> = dishDao.selectDishByFilter(value)

}