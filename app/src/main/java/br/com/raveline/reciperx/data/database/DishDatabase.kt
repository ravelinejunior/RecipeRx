package br.com.raveline.reciperx.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.raveline.reciperx.data.database.dao.DishDao
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.utils.Constants.DISH_DATABASE_NAME

@Database(
    entities = [DishModel::class],
    version = 1,
    exportSchema = true
)
abstract class DishDatabase : RoomDatabase() {

    abstract fun dishDao(): DishDao

    companion object {
        @Volatile
        private var DB_INSTANCE: DishDatabase? = null

        fun getDishDatabase(context: Context): DishDatabase {
            return DB_INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DishDatabase::class.java,
                    DISH_DATABASE_NAME
                ).enableMultiInstanceInvalidation()
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                DB_INSTANCE = instance
                return instance
            }
        }
    }
}