package br.com.raveline.reciperx.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import br.com.raveline.reciperx.utils.Constants.CURRENT_DISH_KEY_NAME
import br.com.raveline.reciperx.utils.Constants.PREFS_KEY_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val currentSavedDish = booleanPreferencesKey(CURRENT_DISH_KEY_NAME)
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_KEY_NAME)

    suspend fun saveRecipesInStore(hasRecipes: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.currentSavedDish] = hasRecipes
        }
    }

    val hasRecipesSaved: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else throw exception
        }.map { prefs ->
            val isRecipeSaved = prefs[PreferencesKeys.currentSavedDish] ?: false
            isRecipeSaved
        }
}