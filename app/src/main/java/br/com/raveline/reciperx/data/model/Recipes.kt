package br.com.raveline.reciperx.data.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Recipes(
    @SerializedName("recipes")
    var recipeModels: List<RecipeModel>
)