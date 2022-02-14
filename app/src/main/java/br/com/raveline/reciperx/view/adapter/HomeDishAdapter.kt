package br.com.raveline.reciperx.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.databinding.ItemAdapterHomeFragmentGridBinding
import br.com.raveline.reciperx.utils.ListDiffUtil
import br.com.raveline.reciperx.view.fragment.dashboard.FavoriteFragment
import br.com.raveline.reciperx.view.fragment.detail.DishDetailFragmentDirections
import br.com.raveline.reciperx.view.fragment.home.HomeFragment
import br.com.raveline.reciperx.view.fragment.home.HomeFragmentDirections
import br.com.raveline.reciperx.view.fragment.notification.RandomFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class HomeDishAdapter(
    private val fragment:Fragment
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

        holder.itemView.setOnClickListener {
            when(fragment){
                is HomeFragment -> {

                    val action = HomeFragmentDirections.actionHomeFragmentToDishDetailFragment(dish)
                    fragment.findNavController().navigate(action)
                }

                is FavoriteFragment -> {

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

    }

    fun setData(dishList: List<DishModel>) {
        val listDiffUtils = ListDiffUtil(dishes, dishList)
        val dispatchersResult = DiffUtil.calculateDiff(listDiffUtils)
        dishes = dishList
        dispatchersResult.dispatchUpdatesTo(this)
    }

}