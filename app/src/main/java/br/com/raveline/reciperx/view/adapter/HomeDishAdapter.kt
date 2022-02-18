package br.com.raveline.reciperx.view.adapter

import android.app.AlertDialog
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
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.databinding.ItemAdapterHomeFragmentGridBinding
import br.com.raveline.reciperx.utils.ListDiffUtil
import br.com.raveline.reciperx.view.fragment.favorite.FavoriteFragment
import br.com.raveline.reciperx.view.fragment.favorite.FavoriteFragmentDirections
import br.com.raveline.reciperx.view.fragment.home.HomeFragment
import br.com.raveline.reciperx.view.fragment.home.HomeFragmentDirections
import br.com.raveline.reciperx.view.fragment.random.RandomFragment
import br.com.raveline.reciperx.viewmodel.FavDishViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class HomeDishAdapter(
    private val fragment: Fragment,
    private val favDishViewModel: FavDishViewModel?
) :
    RecyclerView.Adapter<HomeDishAdapter.MyViewHolder>() {

    private var dishes = listOf<DishModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val hBinding = ItemAdapterHomeFragmentGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(hBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dish = dishes[position]
        holder.bind(dish)
        holder.showPopupOptions(dish)

        holder.itemView.setOnClickListener {
            when (fragment) {
                is HomeFragment -> {
                    val action = HomeFragmentDirections.actionHomeFragmentToDishDetailFragment(dish)
                    fragment.findNavController().navigate(action)
                }

                is FavoriteFragment -> {
                    val action =
                        FavoriteFragmentDirections.actionDashboardFragmentToDishDetailFragment(dish)
                    fragment.findNavController().navigate(action)
                }

                is RandomFragment -> {

                }
            }
        }
    }

    override fun getItemCount(): Int = dishes.size

    inner class MyViewHolder(private val hBinding: ItemAdapterHomeFragmentGridBinding) :
        RecyclerView.ViewHolder(hBinding.root) {

        fun bind(dish: DishModel) {
            hBinding.apply {

                textViewHomeAdapterTitleId.text = dish.title

                Glide.with(imageViewHomeAdapterId.context)
                    .load(dish.image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(
                        ContextCompat.getDrawable(
                            imageViewHomeAdapterId.context,
                            R.drawable.giphy
                        )
                    )
                    .into(imageViewHomeAdapterId)
            }
        }

        fun showPopupOptions(dish: DishModel) {
            hBinding.imageButtonHomeAdapterAddPopupId.setOnClickListener {
                val popup =
                    PopupMenu(fragment.requireContext(), hBinding.imageButtonHomeAdapterAddPopupId)
                popup.menuInflater.inflate(R.menu.menu_adapter_popup, popup.menu)

                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menuPopupEditId -> {

                            val action =
                                HomeFragmentDirections.actionHomeFragmentToNewDishActivity()
                                    .setDish(dish)
                            fragment.findNavController().navigate(action)
                        }

                        R.id.menuPopupDeleteId -> {
                            showDeleteDialog(dish)
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

    private fun showDeleteDialog(dish: DishModel) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle(fragment.resources.getString(R.string.msg_delete_dish))
            .setMessage(fragment.resources.getString(R.string.msg_confirm_delete_dish_message))
            .setPositiveButton("Yes") { dialog, _ ->
                deleteFavorite(dish)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun deleteFavorite(dish: DishModel) {
        favDishViewModel!!.deleteDish(dish)
    }

    fun setData(dishList: List<DishModel>) {
        val listDiffUtils = ListDiffUtil(dishes, dishList)
        val dispatchersResult = DiffUtil.calculateDiff(listDiffUtils)
        dishes = dishList
        dispatchersResult.dispatchUpdatesTo(this)
    }

}