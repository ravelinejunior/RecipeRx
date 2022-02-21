package br.com.raveline.reciperx.data.remote

import br.com.raveline.reciperx.data.model.Recipes
import br.com.raveline.reciperx.utils.Constants.BASE_URL
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RecipesApiService {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RecipesApiServices::class.java)

    suspend fun getRandomRecipes():Single<Recipes> = retrofit.getRandomRecipes()
}