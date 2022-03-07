package br.com.raveline.reciperx.view.fragment.favorite

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import br.com.raveline.reciperx.DishApplication
import br.com.raveline.reciperx.MainActivity
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.databinding.FavoriteFragmentBinding
import br.com.raveline.reciperx.view.adapter.HomeDishAdapter
import br.com.raveline.reciperx.viewmodel.FavDishViewModel
import br.com.raveline.reciperx.viewmodel.factories.FavDishViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    @Inject
    lateinit var favDishViewModelFactory: FavDishViewModelFactory

    private val favDishViewModel: FavDishViewModel by viewModels {
        favDishViewModelFactory
    }

    private lateinit var favoriteBinding: FavoriteFragmentBinding
    private val dishAdapter = HomeDishAdapter(this,null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoriteBinding = FavoriteFragmentBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setObservers()
        return favoriteBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            if (activity is MainActivity) {
                (activity as MainActivity).showBottomNavigationView()
            }
        }
    }

    private fun setObservers() {
        favDishViewModel.favoriteDishes.observe(viewLifecycleOwner) { favDishes ->
            if (favDishes.isEmpty()) {
                favoriteBinding.apply {
                    recyclerViewfavoriteFragmentId.visibility = GONE
                    lottieViewFavoriteFragmentId.visibility = VISIBLE
                }
            } else {
                favoriteBinding.apply {
                    recyclerViewfavoriteFragmentId.visibility = VISIBLE
                    lottieViewFavoriteFragmentId.visibility = GONE
                }

                dishAdapter.setData(favDishes)
            }
        }
    }

    private fun setupRecyclerView() {
        favoriteBinding.recyclerViewfavoriteFragmentId.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = dishAdapter
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.msg_delete_fav_dishes))
            .setMessage(getString(R.string.msg_confirm_delete_message))
            .setPositiveButton("Yes") { dialog, _ ->
                deleteAllFavorites()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    //SUPER MEGA HYPER LAZY METHOD
    private fun deleteAllFavorites() {
        val favDishes = favDishViewModel.favoriteDishes.value!!
        for (dish in favDishes) {
            dish.favoriteDish = false
            favDishViewModel.updateDish(dish)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menuFavDishDeleteId) {
            showDeleteDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fav_dish, menu)
    }

}