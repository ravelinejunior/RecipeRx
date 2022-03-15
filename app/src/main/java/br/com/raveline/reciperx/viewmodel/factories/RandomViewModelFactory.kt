package br.com.raveline.reciperx.viewmodel.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.raveline.reciperx.data.repository.DataStoreRepository
import br.com.raveline.reciperx.data.repository.DishRepository
import br.com.raveline.reciperx.viewmodel.RandomViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RandomViewModelFactory @Inject constructor(
    private val repository: DishRepository,
   @ApplicationContext private val context: Context,
    private val dataStoreRepository: DataStoreRepository
):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RandomViewModel::class.java)){
            return RandomViewModel(repository, context,dataStoreRepository) as T
        }
        throw IllegalArgumentException("Class doesn't match!")
    }

}