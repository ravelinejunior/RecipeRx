package br.com.raveline.reciperx.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.raveline.reciperx.utils.Constants
import br.com.raveline.reciperx.utils.Constants.DISH_TABLE_NAME
import kotlinx.parcelize.Parcelize
import org.jsoup.Jsoup

@Parcelize
@Entity(tableName = DISH_TABLE_NAME)
data class DishModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val image: String,
    val imageSource: String,
    val title: String,
    val type: String,
    val category: String,
    val ingredients: String,
    val cookingTime: String,
    val directionsToCook: String,
    var favoriteDish: Boolean = false,
    var isFromRandom:Boolean = false,
) : Parcelable

