package br.com.raveline.reciperx.data.remote

import br.com.raveline.reciperx.data.model.Recipes
import br.com.raveline.reciperx.utils.Constants.API_KEY
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

sealed interface RecipesApiServices{
    @GET("random")
     fun getRandomRecipes(
        @Query("apiKey") apiKey:String = API_KEY,
        @Query("number") number:Int = 30,
        @Query("tags") tags:String
    ):Single<Recipes>
}