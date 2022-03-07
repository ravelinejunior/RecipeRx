package br.com.raveline.reciperx.view.fragment.home

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.raveline.reciperx.DishApplication
import br.com.raveline.reciperx.MainActivity
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.databinding.DialogCustomSpinnerDataBinding
import br.com.raveline.reciperx.databinding.HomeFragmentBinding
import br.com.raveline.reciperx.utils.Constants.ALL_ITEMS_STRING
import br.com.raveline.reciperx.utils.Constants.DISH_FILTER_SELECTION
import br.com.raveline.reciperx.utils.Constants.dishTypes
import br.com.raveline.reciperx.view.adapter.CustomSpinnerAdapter
import br.com.raveline.reciperx.view.adapter.HomeDishAdapter
import br.com.raveline.reciperx.viewmodel.FavDishViewModel
import br.com.raveline.reciperx.viewmodel.RandomViewModel
import br.com.raveline.reciperx.viewmodel.factories.FavDishViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var homeFragmentBinding: HomeFragmentBinding

    @Inject
    lateinit var favDishViewModelFactory: FavDishViewModelFactory

    private val favDishViewModel: FavDishViewModel by viewModels {
        favDishViewModelFactory
    }

    private lateinit var customListDialog: Dialog
    private lateinit var homeDishAdapter: HomeDishAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return homeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            if (activity is MainActivity) {
                (activity as MainActivity).showBottomNavigationView()
            }
        }

        favDishViewModel.allDish.observe(viewLifecycleOwner) { dishes ->
            verifyDishes(dishes)
        }

    }

    fun filterSelection(filterSelection:String){
        customListDialog.dismiss()

        if(filterSelection == ALL_ITEMS_STRING){
            favDishViewModel.allDish.observe(viewLifecycleOwner) { dishes ->
                verifyDishes(dishes)
            }
        }else{
            favDishViewModel.getDishesByFilter(filterSelection).observe(viewLifecycleOwner){ dishes ->
                verifyDishes(dishes)
            }
        }
    }

    private fun filterDishesDialog() {

        customListDialog = Dialog(requireContext())
        val dialogBinding = DialogCustomSpinnerDataBinding.inflate(layoutInflater)

        customListDialog.setContentView(dialogBinding.root)
        dialogBinding.textViewCustomSpinnerId.text =
            resources.getString(R.string.title_select_item_to_filter)
        val dishTypes = dishTypes()
        dishTypes.add(0, ALL_ITEMS_STRING)

        dialogBinding.recyclerViewCustomSpinnerId.layoutManager =
            LinearLayoutManager(requireContext())

        val cAdapter = CustomSpinnerAdapter(requireActivity(),this, DISH_FILTER_SELECTION, dishTypes)

        dialogBinding.recyclerViewCustomSpinnerId.adapter = cAdapter

        customListDialog.show()

    }

    private fun setupRecyclerView() {
        homeDishAdapter = HomeDishAdapter(this, favDishViewModel)
        homeFragmentBinding.recyclerViewHomeFragmentId.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = homeDishAdapter
        }
    }

    private fun verifyDishes(dishes:List<DishModel>){
        if (dishes.isNotEmpty()) {
            homeDishAdapter.setData(dishes)
            homeFragmentBinding.recyclerViewHomeFragmentId.apply {
                visibility = VISIBLE
            }
            homeFragmentBinding.lottieViewHomeFragmentId.visibility = GONE
        } else {
            homeFragmentBinding.recyclerViewHomeFragmentId.apply {
                visibility = GONE
            }
            homeFragmentBinding.lottieViewHomeFragmentId.visibility = VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_new_dishe, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menuNewDishAddId -> {
                findNavController().navigate(R.id.action_homeFragment_to_newDishActivity)
                return true
            }

            R.id.menuNewDishFilterId -> {
                filterDishesDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}