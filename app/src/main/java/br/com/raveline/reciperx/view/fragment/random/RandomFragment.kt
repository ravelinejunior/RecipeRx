package br.com.raveline.reciperx.view.fragment.random

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.raveline.reciperx.MainActivity
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.databinding.RandomFragmentBinding
import br.com.raveline.reciperx.listeners.NetworkListeners
import br.com.raveline.reciperx.utils.SystemFunctions.observeOnce
import br.com.raveline.reciperx.view.adapter.RandomRecipesAdapter
import br.com.raveline.reciperx.viewmodel.RandomViewModel
import br.com.raveline.reciperx.viewmodel.factories.RandomViewModelFactory
import com.airbnb.lottie.LottieDrawable
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

    @Inject
    lateinit var networkListeners: NetworkListeners

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        randomBinding = RandomFragmentBinding.inflate(inflater, container, false)
        randomAdapter = RandomRecipesAdapter(this, randomViewModel)
        setObservables()

        getOfflineData(false)

        randomBinding!!.swipeRefreshLayoutRandomFragment.apply {
            setOnRefreshListener {
                getOfflineData(true)
                isRefreshing = false
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

    private fun getOfflineData(swipe: Boolean) {
        randomViewModel.allRecipes.observeOnce(viewLifecycleOwner) {
            randomViewModel.getOfflineRecipes(swipe)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setObservables() {
        lifecycleScope.launch(Main) {
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
                    getOfflineData(true)
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

            if (randomViewModel.allRecipes.value.isNullOrEmpty()) {
                randomViewModel.recipeRecipesLiveData.observe(viewLifecycleOwner) { recipes ->
                    if (recipes.recipeModels.isNotEmpty()) {
                        randomAdapter.setData(recipes.recipeModels)
                        setSuccessfulDataDisplay(this)
                    }
                }
            } else {
                randomViewModel.allRecipes.observeOnce(viewLifecycleOwner) { recipes ->
                    randomAdapter.setData(recipes)
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

    override fun onDestroy() {
        super.onDestroy()
        randomBinding = null
    }

}