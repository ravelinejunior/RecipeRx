package br.com.raveline.reciperx.view.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.data.model.RecipeModel
import br.com.raveline.reciperx.data.model.Recipes
import br.com.raveline.reciperx.databinding.ItemAdapterHomeFragmentGridBinding
import br.com.raveline.reciperx.utils.ListDiffUtil
import br.com.raveline.reciperx.utils.SystemFunctions.loadImage
import br.com.raveline.reciperx.view.fragment.home.HomeFragment
import br.com.raveline.reciperx.view.fragment.home.HomeFragmentDirections
import br.com.raveline.reciperx.view.fragment.random.RandomFragment
import br.com.raveline.reciperx.view.fragment.random.RandomFragmentDirections
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class RandomRecipesAdapter(
    private val fragment: Fragment,
) :
    RecyclerView.Adapter<RandomRecipesAdapter.MyViewHolder>() {

    private lateinit var recipes: Recipes
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
        val recipe = recipes.recipeModels[position]
        holder.bind(recipe)
        holder.showPopupOptions(recipe)

        holder.itemView.setOnClickListener {
            when (fragment) {
                is RandomFragment -> {
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToDishDetailFragment(null, recipe)
                    fragment.findNavController().navigate(action)
                }
            }
        }
    }

    override fun getItemCount(): Int = recipes.recipeModels.size

    inner class MyViewHolder(private val hBinding: ItemAdapterHomeFragmentGridBinding) :
        RecyclerView.ViewHolder(hBinding.root) {

        fun bind(recipe: RecipeModel) {
            hBinding.apply {

                textViewHomeAdapterTitleId.text = recipe.title

               if (recipe.image.isNullOrEmpty()){
                   Glide.with(imageViewHomeAdapterId.context)
                       .load(drawable)
                       .diskCacheStrategy(DiskCacheStrategy.ALL)
                       .into(imageViewHomeAdapterId)
               }else{

                   loadImage(imageViewHomeAdapterId,recipe.image)

               }
            }
        }

        fun showPopupOptions(recipe: RecipeModel) {
            hBinding.imageButtonHomeAdapterAddPopupId.setOnClickListener {
                val popup =
                    PopupMenu(fragment.requireContext(), hBinding.imageButtonHomeAdapterAddPopupId)
                popup.menuInflater.inflate(R.menu.menu_adapter_popup, popup.menu)

                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menuPopupEditId -> {

                            val action =
                                RandomFragmentDirections.actionNotificationFragmentToDishDetailFragment(
                                    null,
                                    recipe
                                )
                            fragment.findNavController().navigate(action)
                        }
                    }
                    popup.dismiss()
                    true
                }

                popup.show()
            }

            if (fragment is HomeFragment) {
                hBinding.imageButtonHomeAdapterAddPopupId.visibility = VISIBLE
            } else {
                hBinding.imageButtonHomeAdapterAddPopupId.visibility = GONE

            }
        }

    }


    fun setData(recipesList: Recipes) {
        recipes = Recipes(emptyList())
        val listDiffUtils = ListDiffUtil(recipes.recipeModels, recipesList.recipeModels)
        val dispatchersResult = DiffUtil.calculateDiff(listDiffUtils)
        recipes = recipesList
        dispatchersResult.dispatchUpdatesTo(this)
    }

}