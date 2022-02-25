package br.com.raveline.reciperx.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.data.model.RecipeModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.jsoup.Jsoup
import java.io.ByteArrayOutputStream
import kotlin.random.Random


object SystemFunctions {
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getProgressDrawable(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 10f
            centerRadius = 50f
            start()
        }
    }

    fun ImageView.loadImage(uri: String?, circularProgressDrawable: CircularProgressDrawable) {
        val options = RequestOptions()
            .placeholder(circularProgressDrawable)
            .error(com.google.android.material.R.drawable.ic_mtrl_chip_close_circle)

        Glide.with(context).setDefaultRequestOptions(options)
            .load(uri).into(this)
    }

    @JvmStatic
    @BindingAdapter("android:imageUrl")
    fun loadImage(view: ImageView, url: String?) {
        view.loadImage(url, getProgressDrawable(view.context))
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Image", null)
        return Uri.parse(path)
    }

    fun recipeToDish(recipe: RecipeModel): DishModel =
        DishModel(
            image = recipe.image ?: String(),
            imageSource = Constants.DISH_IMAGE_SOURCE_REMOTE,
            type = recipe.dishTypes?.map {
                it
            }.toString().replace("[", "").replace("]", ""),
            title = recipe.title ?: String(),
            category = recipe.diets?.map {
                it
            }.toString().replace("[", "").replace("]", ""),
            cookingTime = recipe.readyInMinutes?.toString() ?: String(),
            ingredients = Jsoup.parse(recipe.summary.toString()).text() ?: String(),
            directionsToCook = Jsoup.parse(recipe.instructions.toString()).text() ?: String(),

        )

}