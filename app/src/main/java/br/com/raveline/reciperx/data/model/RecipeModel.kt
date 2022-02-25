package br.com.raveline.reciperx.data.model


import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import br.com.raveline.reciperx.data.database.converter.Converter
import br.com.raveline.reciperx.utils.Constants.RECIPES_TABLE_NAME
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
@Entity(tableName = RECIPES_TABLE_NAME)
data class RecipeModel(
    @SerializedName("aggregateLikes")
    val aggregateLikes: Int?,
    @SerializedName("cheap")
    val cheap: Boolean?,
    @SerializedName("creditsText")
    val creditsText: String?,
    @TypeConverters(Converter::class)
    @SerializedName("cuisines")
    val cuisines: List<String>?,
    @SerializedName("dairyFree")
    val dairyFree: Boolean?,
    @TypeConverters(Converter::class)
    @SerializedName("diets")
    val diets: List<String>?,
    @SerializedName("dishTypes")
    @TypeConverters(Converter::class)
    val dishTypes: List<String>?,
    @SerializedName("gaps")
    val gaps: String?,
    @SerializedName("glutenFree")
    val glutenFree: Boolean?,
    @SerializedName("healthScore")
    val healthScore: Int?,

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int?,

    @SerializedName("image")
    val image: String?,
    @SerializedName("imageType")
    val imageType: String?,
    @SerializedName("instructions")
    val instructions: String?,
    @SerializedName("license")
    val license: String?,
    @SerializedName("lowFodmap")
    val lowFoodMap: Boolean?,
    @SerializedName("occasions")
    @TypeConverters(Converter::class)
    val occasions: List<String>?,
    @SerializedName("originalId")
    @TypeConverters(Converter::class)
    val originalId: String?,
    @SerializedName("pricePerServing")
    val pricePerServing: Double?,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int?,
    @SerializedName("servings")
    val servings: Int?,
    @SerializedName("sourceName")
    val sourceName: String?,
    @SerializedName("sourceUrl")
    val sourceUrl: String?,
    @SerializedName("spoonacularScore")
    val spoonacularScore: Int?,
    @SerializedName("spoonacularSourceUrl")
    val spoonacularSourceUrl: String?,
    @SerializedName("summary")
    val summary: String?,
    @SerializedName("sustainable")
    val sustainable: Boolean?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("vegan")
    val vegan: Boolean?,
    @SerializedName("vegetarian")
    val vegetarian: Boolean?,
    @SerializedName("veryHealthy")
    val veryHealthy: Boolean?,
    @SerializedName("veryPopular")
    val veryPopular: Boolean?,
    @SerializedName("weightWatcherSmartPoints")
    val weightWatcherSmartPoints: Int?
) : Serializable
