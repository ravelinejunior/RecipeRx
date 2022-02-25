package br.com.raveline.reciperx.data.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.Entity

@Keep
data class Recipes(
    @SerializedName("recipes")
    var recipeModels: List<RecipeModel>
)