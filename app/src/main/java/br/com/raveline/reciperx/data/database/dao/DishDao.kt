package br.com.raveline.reciperx.data.database.dao

import androidx.room.*
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.data.model.Recipes
import kotlinx.coroutines.flow.Flow

@Dao
sealed interface DishDao {

    @Query("SELECT * FROM DISH_TABLE WHERE isFromRandom = 0 ORDER BY ID DESC")
    fun selectAllDishes(): Flow<List<DishModel>>

    @Query("SELECT * FROM DISH_TABLE WHERE type == :value ORDER BY ID DESC")
    fun selectDishByFilter(value: String): Flow<List<DishModel>>

    @Query("SELECT * FROM DISH_TABLE WHERE favoriteDish = 1 ORDER BY ID DESC")
    fun selectFavoritesDishes(): Flow<List<DishModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDish(dishModel: DishModel)

    @Update
    suspend fun updateDish(dishModel: DishModel)

    @Delete
    suspend fun deleteFavoriteDish(dishModel: DishModel)

    @Query("DELETE FROM DISH_TABLE")
    suspend fun deleteAllDishes()

}