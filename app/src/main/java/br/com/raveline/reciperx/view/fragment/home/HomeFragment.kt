package br.com.raveline.reciperx.view.fragment.home

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import br.com.raveline.reciperx.DishApplication
import br.com.raveline.reciperx.MainActivity
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.databinding.HomeFragmentBinding
import br.com.raveline.reciperx.view.adapter.HomeDishAdapter
import br.com.raveline.reciperx.viewmodel.FavDishViewModel
import br.com.raveline.reciperx.viewmodel.HomeViewModel
import br.com.raveline.reciperx.viewmodel.factories.FavDishViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var homeFragmentBinding: HomeFragmentBinding
    private val homeViewModel: HomeViewModel by viewModels()

    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as DishApplication).repository)
    }

    private lateinit var homeDishAdapter:HomeDishAdapter

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
            dishes.let {
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
        }

    }

    private fun setupRecyclerView() {
        homeDishAdapter = HomeDishAdapter(this,favDishViewModel)
        homeFragmentBinding.recyclerViewHomeFragmentId.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = homeDishAdapter
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
        }
        return super.onOptionsItemSelected(item)
    }

}