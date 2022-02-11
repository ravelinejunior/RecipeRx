package br.com.raveline.reciperx.data.database.dao

import androidx.room.*
import br.com.raveline.reciperx.data.model.DishModel

@Dao
sealed interface DishDao {

    @Query("SELECT * FROM DISH_TABLE ORDER BY ID")
    suspend fun selectAllDishes(): List<DishModel>

    @Update
    suspend fun updateDish(dishModel: DishModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDish(dishModel: DishModel)

    @Delete
    suspend fun deleteFavoriteDish(dishModel: DishModel)

    @Query("DELETE FROM DISH_TABLE")
    suspend fun deleteAllDishes()

}