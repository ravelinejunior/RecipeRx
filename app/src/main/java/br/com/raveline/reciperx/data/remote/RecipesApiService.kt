package br.com.raveline.reciperx.data.remote

import br.com.raveline.reciperx.data.model.Recipes
import br.com.raveline.reciperx.utils.Constants.BASE_URL
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class RecipesApiService {

    private val okHttpInterceptor = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.HEADERS
            )
        )
        .addInterceptor(
            HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.BODY
            )
        )
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpInterceptor)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RecipesApiServices::class.java)

    fun getRandomRecipes(tag: String): Single<Recipes> = retrofit.getRandomRecipes(tags = tag)
}