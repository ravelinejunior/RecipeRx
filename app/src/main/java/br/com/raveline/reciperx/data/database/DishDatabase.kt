package br.com.raveline.reciperx.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.raveline.reciperx.data.database.converter.Converter
import br.com.raveline.reciperx.data.database.dao.DishDao
import br.com.raveline.reciperx.data.database.dao.RecipeDao
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.data.model.RecipeModel

@Database(
    entities = [DishModel::class, RecipeModel::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converter::class)
abstract class DishDatabase : RoomDatabase() {

    abstract fun dishDao(): DishDao
    abstract fun recipeDao(): RecipeDao

}
