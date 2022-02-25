package br.com.raveline.reciperx.view.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.data.model.RecipeModel
import br.com.raveline.reciperx.data.model.Recipes
import br.com.raveline.reciperx.databinding.ItemAdapterHomeFragmentGridBinding
import br.com.raveline.reciperx.utils.ListDiffUtil
import br.com.raveline.reciperx.utils.SystemFunctions.loadImage
import br.com.raveline.reciperx.utils.SystemFunctions.recipeToDish
import br.com.raveline.reciperx.view.fragment.random.RandomFragment
import br.com.raveline.reciperx.view.fragment.random.RandomFragmentDirections
import br.com.raveline.reciperx.viewmodel.RandomViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlin.random.Random

class RandomRecipesAdapter(
    private val fragment: Fragment,
    private val randomViewModel: RandomViewModel
) :
    RecyclerView.Adapter<RandomRecipesAdapter.MyViewHolder>() {

    private lateinit var recipes: List<RecipeModel>
    private var drawable =
        "https://www.prestashop.com/sites/default/files/styles/blog_750x320/public/blog/pt/files/2013/12/http_code_404_error.jpg?itok=uFS5CFuQ"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val hBinding = ItemAdapterHomeFragmentGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(hBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
        holder.showPopupOptions(recipeToDish(recipe))

        holder.itemView.setOnClickListener {
            when (fragment) {
                is RandomFragment -> {
                    val dish = recipeToDish(recipe)
                    dish.isFromRandom = true
                    CoroutineScope(IO).launch {
                        randomViewModel.saveSelectedOnDatabase(dish)
                    }
                    val action =
                        RandomFragmentDirections.actionRandomFragmentToDishDetailFragment(dish)
                    fragment.findNavController().navigate(action)

                }
            }
        }
    }

    override fun getItemCount(): Int = recipes.size

    inner class MyViewHolder(private val hBinding: ItemAdapterHomeFragmentGridBinding) :
        RecyclerView.ViewHolder(hBinding.root) {

        fun bind(recipe: RecipeModel) {
            hBinding.apply {

                textViewHomeAdapterTitleId.text = recipe.title

                if (recipe.image.isNullOrEmpty()) {
                    Glide.with(imageViewHomeAdapterId.context)
                        .load(drawable)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageViewHomeAdapterId)
                } else {

                    loadImage(imageViewHomeAdapterId, recipe.image)

                }
            }
        }

        fun showPopupOptions(dish: DishModel) {
            if (fragment is RandomFragment) {
                hBinding.imageButtonHomeAdapterAddPopupId.setOnClickListener {
                    val popup =
                        PopupMenu(
                            fragment.requireContext(),
                            hBinding.imageButtonHomeAdapterAddPopupId
                        )
                    popup.menuInflater.inflate(R.menu.menu_random_adapter_popup, popup.menu)

                    popup.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.menuRandomPopupEditId -> {
                                showAddDialog(dish)
                            }
                        }
                        popup.dismiss()
                        true
                    }

                    popup.show()
                }

            }

            if (fragment is RandomFragment) {
                hBinding.imageButtonHomeAdapterAddPopupId.visibility = VISIBLE
            } else {
                hBinding.imageButtonHomeAdapterAddPopupId.visibility = GONE
            }
        }

         fun showAddDialog(dish: DishModel) {
            AlertDialog.Builder(fragment.requireContext())
                .setTitle(fragment.resources.getString(R.string.msg_insert_on_main))
                .setMessage(fragment.resources.getString(R.string.msg_insert_this_on_your))
                .setPositiveButton("Yes") { dialog, _ ->
                    CoroutineScope(IO).launch {
                        randomViewModel.saveSelectedOnDatabase(dish)
                    }
                    dialog.dismiss()
                    Toast.makeText(fragment.context, "Added to main dishes!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

    }

    fun setData(recipesList: List<RecipeModel>) {
        recipes = emptyList()
        val listDiffUtils = ListDiffUtil(recipes, recipesList)
        val dispatchersResult = DiffUtil.calculateDiff(listDiffUtils)
        recipes = recipesList
        dispatchersResult.dispatchUpdatesTo(this)
    }

}