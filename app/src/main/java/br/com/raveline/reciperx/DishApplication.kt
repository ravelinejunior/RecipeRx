package br.com.raveline.reciperx

import android.app.Application
import br.com.raveline.reciperx.data.database.DishDatabase
import br.com.raveline.reciperx.data.repository.DishRepository

class DishApplication : Application() {
    private val database by lazy { DishDatabase.getDishDatabase(this) }

    val repository by lazy { DishRepository(database.dishDao(), database.recipeDao()) }
}