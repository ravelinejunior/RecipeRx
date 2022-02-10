package br.com.raveline.reciperx.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.raveline.reciperx.databinding.ItemAdapterCustomSpinnerDataBinding
import br.com.raveline.reciperx.view.activity.NewDishActivity

class CustomSpinnerAdapter(
    private val activity:Activity,
    private val selected: String,
    private val listItems:List<String>
) : RecyclerView.Adapter<CustomSpinnerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemAdapterCustomSpinnerDataBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = listItems[position]
        holder.customSpinnerDataBinding.textViewAdapterCustomSpinnerId.text = item
        holder.customSpinnerDataBinding.constraintLayoutAdapterCustomSpinnerId.setOnClickListener {
            if (activity is NewDishActivity){
                activity.selectedListItem(item,selected)
            }
        }
    }

    override fun getItemCount():Int = listItems.size

    inner class MyViewHolder(val customSpinnerDataBinding: ItemAdapterCustomSpinnerDataBinding) :
        RecyclerView.ViewHolder(customSpinnerDataBinding.root)

}