package br.com.raveline.reciperx.di

import android.content.Context
import androidx.room.Room
import br.com.raveline.reciperx.data.database.DishDatabase
import br.com.raveline.reciperx.data.database.dao.DishDao
import br.com.raveline.reciperx.data.database.dao.RecipeDao
import br.com.raveline.reciperx.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): DishDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            DishDatabase::class.java,
            Constants.DISH_DATABASE_NAME
        ).enableMultiInstanceInvalidation()
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDishDao(dishDatabase: DishDatabase): DishDao =
        dishDatabase.dishDao()

    @Provides
    @Singleton
    fun provideRecipeDao(dishDatabase: DishDatabase): RecipeDao =
        dishDatabase.recipeDao()
}