package br.com.raveline.reciperx.view.fragment.random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.raveline.reciperx.DishApplication
import br.com.raveline.reciperx.MainActivity
import br.com.raveline.reciperx.databinding.RandomFragmentBinding
import br.com.raveline.reciperx.view.adapter.RandomRecipesAdapter
import br.com.raveline.reciperx.viewmodel.RandomViewModel
import br.com.raveline.reciperx.viewmodel.factories.RandomViewModelFactory
import kotlinx.coroutines.flow.collect

class RandomFragment : Fragment() {

    private var randomBinding: RandomFragmentBinding? = null
    private val randomViewModel: RandomViewModel by viewModels {
        RandomViewModelFactory((requireActivity().application as DishApplication).repository)
    }
    private lateinit var randomAdapter: RandomRecipesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        randomBinding = RandomFragmentBinding.inflate(inflater, container, false)
        randomAdapter = RandomRecipesAdapter(this, randomViewModel)
        setObservables()
        return randomBinding!!.root
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launchWhenResumed {

            randomViewModel.getRandomRecipes()

            if (activity is MainActivity) {
                (activity as MainActivity).showBottomNavigationView()
            }

            setupRecyclerView()
        }
    }

    private fun setObservables() {
        lifecycleScope.launchWhenStarted {
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
                        randomBinding?.run {

                            randomViewModel.recipeRecipesLiveData.observe(viewLifecycleOwner) { recipes ->
                                if (recipes.recipeModels.isNotEmpty()) {
                                    randomAdapter.setData(recipes)
                                    progressBarRandomFragment.visibility = GONE
                                    lottieViewRandomFragmentId.visibility = GONE
                                    recyclerViewRandomFragmentId.adapter = randomAdapter
                                    recyclerViewRandomFragmentId.visibility = VISIBLE
                                } else {
                                    randomBinding?.run {
                                        progressBarRandomFragment.visibility = GONE
                                        lottieViewRandomFragmentId.visibility = VISIBLE
                                        recyclerViewRandomFragmentId.visibility = GONE
                                    }
                                }

                            }
                        }
                    }

                    RandomViewModel.UiState.Error -> {
                        randomBinding?.run {
                            progressBarRandomFragment.visibility = GONE
                            lottieViewRandomFragmentId.visibility = VISIBLE
                            recyclerViewRandomFragmentId.visibility = GONE
                        }
                    }
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

    override fun onDestroy() {
        super.onDestroy()
        randomBinding = null
    }

}