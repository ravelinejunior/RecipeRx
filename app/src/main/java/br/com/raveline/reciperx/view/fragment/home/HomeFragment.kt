package br.com.raveline.reciperx.view.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import br.com.raveline.reciperx.DishApplication
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.databinding.HomeFragmentBinding
import br.com.raveline.reciperx.view.activity.NewDishActivity
import br.com.raveline.reciperx.view.adapter.HomeDishAdapter
import br.com.raveline.reciperx.viewmodel.FavDishViewModel
import br.com.raveline.reciperx.viewmodel.HomeViewModel
import br.com.raveline.reciperx.viewmodel.factories.FavDishViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var homeFragmentBinding: HomeFragmentBinding
    private val homeViewModel: HomeViewModel by viewModels()

    private val homeDishAdapter = HomeDishAdapter()

    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as DishApplication).repository)
    }

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

        favDishViewModel.allDish.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                if (dishes.isNotEmpty()) {
                    homeDishAdapter.setData(dishes)
                    homeFragmentBinding.recyclerViewHomeFragmentId.apply {
                        adapter = homeDishAdapter
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
        homeFragmentBinding.recyclerViewHomeFragmentId.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_new_dishe, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menuNewDishAddId -> {
                val intent = Intent(context, NewDishActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}