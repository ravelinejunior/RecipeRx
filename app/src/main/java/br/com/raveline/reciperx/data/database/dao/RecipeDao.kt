package br.com.raveline.reciperx.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.raveline.reciperx.data.model.RecipeModel
import br.com.raveline.reciperx.data.model.Recipes
import kotlinx.coroutines.flow.Flow

@Dao
sealed interface RecipeDao {
    @Query("SELECT * FROM RECIPES_TABLE ORDER BY id DESC")
    fun selectAllFromRecipes(): Flow<List<RecipeModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeModel>)

    @Query("DELETE FROM RECIPES_TABLE")
    suspend fun deleteAllRecipes()
}