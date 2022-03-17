package br.com.raveline.reciperx.view.fragment.random

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.raveline.reciperx.MainActivity
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.databinding.DialogCustomSpinnerDataBinding
import br.com.raveline.reciperx.databinding.RandomFragmentBinding
import br.com.raveline.reciperx.listeners.NetworkListeners
import br.com.raveline.reciperx.utils.Constants
import br.com.raveline.reciperx.utils.SystemFunctions.observeOnce
import br.com.raveline.reciperx.view.adapter.CustomSpinnerAdapter
import br.com.raveline.reciperx.view.adapter.RandomRecipesAdapter
import br.com.raveline.reciperx.viewmodel.RandomViewModel
import br.com.raveline.reciperx.viewmodel.factories.RandomViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RandomFragment : Fragment() {

    private var randomBinding: RandomFragmentBinding? = null

    @Inject
    lateinit var randomViewModelFactory: RandomViewModelFactory

    private val randomViewModel: RandomViewModel by viewModels {
        randomViewModelFactory
    }

    private lateinit var randomAdapter: RandomRecipesAdapter

    private lateinit var customListDialog: Dialog

    private var mTag = "main course"

    @Inject
    lateinit var networkListeners: NetworkListeners

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        randomBinding = RandomFragmentBinding.inflate(inflater, container, false)
        randomAdapter = RandomRecipesAdapter(this, randomViewModel)
        setObservables()

        lifecycleScope.launch {
            getData(false, mTag)
        }

        randomBinding!!.swipeRefreshLayoutRandomFragment.apply {
            setOnRefreshListener {
                lifecycleScope.launch {
                    getData(true, mTag)
                    isRefreshing = false
                }
            }

        }
        return randomBinding!!.root
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launchWhenResumed {

            if (activity is MainActivity) {
                (activity as MainActivity).showBottomNavigationView()
            }

            setupRecyclerView()
        }
    }

    private fun getData(swipe: Boolean, tag: String) {
        lifecycleScope.launch {
            randomViewModel.allRecipes.observeOnce(viewLifecycleOwner) { recipes ->
                if (recipes.isNotEmpty() && !swipe) {
                    randomAdapter.setData(recipes)
                    setSuccessfulDataDisplay(randomBinding!!)
                } else {
                    requestApiData(tag)
                }
            }
        }
    }

    private fun requestApiData(tag: String) {
        randomViewModel.getRecipes(true, tag)

        lifecycleScope.launch {
            randomViewModel.uiStateFlow.collect { uiState ->
                when (uiState) {

                    RandomViewModel.UiState.Initial,
                    RandomViewModel.UiState.Loading -> {
                        randomBinding?.run {
                            progressBarRandomFragment.visibility = VISIBLE
                            recyclerViewRandomFragmentId.visibility = GONE
                            lottieViewRandomFragmentId.visibility = GONE
                        }
                    }

                    RandomViewModel.UiState.Success -> {
                        setupSuccessRecipe()
                    }

                    RandomViewModel.UiState.Error -> {
                        randomBinding?.run {
                            progressBarRandomFragment.visibility = GONE
                            lottieViewRandomFragmentId.visibility = VISIBLE
                            recyclerViewRandomFragmentId.visibility = GONE
                        }

                    }
                    RandomViewModel.UiState.NoConnection -> {
                        getNoConnectionDisplay()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setObservables() {
        lifecycleScope.launch {
            randomViewModel.uiStateFlow.collect { uiState ->
                when (uiState) {

                    RandomViewModel.UiState.Initial,
                    RandomViewModel.UiState.Loading -> {
                        randomBinding?.run {
                            progressBarRandomFragment.visibility = VISIBLE
                            recyclerViewRandomFragmentId.visibility = GONE
                            lottieViewRandomFragmentId.visibility = GONE
                        }
                    }

                    RandomViewModel.UiState.Success -> {
                        setupSuccessRecipe()
                    }

                    RandomViewModel.UiState.Error -> {
                        randomBinding?.run {
                            progressBarRandomFragment.visibility = GONE
                            lottieViewRandomFragmentId.visibility = VISIBLE
                            recyclerViewRandomFragmentId.visibility = GONE
                        }

                    }
                    RandomViewModel.UiState.NoConnection -> {
                        getNoConnectionDisplay()
                    }
                }
            }

            networkListeners.checkNetworkAvailability(requireContext()).collect { status ->
                if (!status) {
                    getNoConnectionDisplay()
                } else {
                    getData(true, mTag)
                }
            }

        }


    }

    private fun setupRecyclerView() {
        randomBinding?.recyclerViewRandomFragmentId?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupSuccessRecipe() {
        randomBinding?.run {
            randomViewModel.recipeRecipesLiveData.observe(viewLifecycleOwner) { recipes ->
                if (recipes.recipeModels.isNotEmpty()) {
                    randomAdapter.setData(recipes.recipeModels)
                    setSuccessfulDataDisplay(this)
                }
            }
        }
    }

    private fun getNoConnectionDisplay() {
        randomBinding?.run {
            progressBarRandomFragment.visibility = GONE
            recyclerViewRandomFragmentId.visibility = GONE
            lottieViewRandomFragmentId.apply {
                visibility = VISIBLE
                setAnimation(R.raw.no_internet_connection)
                playAnimation()
            }
        }
    }

    private fun setSuccessfulDataDisplay(randomFragmentBinding: RandomFragmentBinding) {
        randomFragmentBinding.apply {
            progressBarRandomFragment.visibility = GONE
            lottieViewRandomFragmentId.visibility = GONE
            recyclerViewRandomFragmentId.adapter = randomAdapter
            recyclerViewRandomFragmentId.visibility = VISIBLE
        }
    }

    private fun filterDishesDialog() {

        customListDialog = Dialog(requireContext())
        val dialogBinding = DialogCustomSpinnerDataBinding.inflate(layoutInflater)

        customListDialog.setContentView(dialogBinding.root)
        dialogBinding.textViewCustomSpinnerId.text =
            resources.getString(R.string.title_select_item_to_filter)
        val dishTypes = Constants.dishRecipeCategoryType()

        dialogBinding.recyclerViewCustomSpinnerId.layoutManager =
            LinearLayoutManager(requireContext())

        val cAdapter = CustomSpinnerAdapter(
            requireActivity(),
            this,
            Constants.DISH_FILTER_SELECTION,
            dishTypes
        )

        dialogBinding.recyclerViewCustomSpinnerId.adapter = cAdapter

        customListDialog.show()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menuFilterRemoteDishId -> {
                filterDishesDialog()
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_random, menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        randomBinding = null
    }

    fun getRandomByCategory(item: String) {
        mTag = item
        lifecycleScope.launch {
            getData(true, mTag)
        }

        customListDialog.dismiss()
    }

}