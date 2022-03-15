package br.com.raveline.reciperx.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import br.com.raveline.reciperx.databinding.ItemAdapterCustomSpinnerDataBinding
import br.com.raveline.reciperx.view.activity.NewDishActivity
import br.com.raveline.reciperx.view.fragment.home.HomeFragment
import br.com.raveline.reciperx.view.fragment.random.RandomFragment

class CustomSpinnerAdapter(
    private val activity:Activity,
    private val fragment:Fragment?,
    private val selected: String,
    private val listItems:List<String>,
) : RecyclerView.Adapter<CustomSpinnerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemAdapterCustomSpinnerDataBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = listItems[position]
        holder.customSpinnerDataBinding.textViewAdapterCustomSpinnerId.text = item.capitalize(Locale.current)
        holder.customSpinnerDataBinding.constraintLayoutAdapterCustomSpinnerId.setOnClickListener {
            if (activity is NewDishActivity){
                activity.selectedListItem(item,selected)
            }
            if(fragment is HomeFragment){
                fragment.filterSelection(item)
            }

            if(fragment is RandomFragment){
                fragment.getRandomByCategory(item)
            }
        }
    }

    override fun getItemCount():Int = listItems.size

    inner class MyViewHolder(val customSpinnerDataBinding: ItemAdapterCustomSpinnerDataBinding) :
        RecyclerView.ViewHolder(customSpinnerDataBinding.root)

}