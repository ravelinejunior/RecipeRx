package br.com.raveline.reciperx.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.databinding.ItemAdapterHomeFragmentGridBinding
import br.com.raveline.reciperx.utils.ListDiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class HomeDishAdapter :
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