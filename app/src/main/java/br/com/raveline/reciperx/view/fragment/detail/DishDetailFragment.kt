package br.com.raveline.reciperx.view.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import br.com.raveline.reciperx.MainActivity
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.databinding.DishDetailFragmentBinding
import br.com.raveline.reciperx.viewmodel.DishDetailViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class DishDetailFragment : Fragment() {

    private lateinit var viewModel: DishDetailViewModel
    private lateinit var dBinding: DishDetailFragmentBinding
    private val args: DishDetailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dBinding = DishDetailFragmentBinding.inflate(inflater, container, false)
        dBinding.dish = args.dish
        dBinding.lifecycleOwner = this

        displayData(args.dish)

        return dBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            if (activity is MainActivity) {
                (activity as MainActivity).hideBottomNavigationView()
            }
        }
    }

    private fun displayData(dish: DishModel) {
        dBinding.apply {
            Glide.with(requireContext()).load(dish.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.giphy
                    )
                )
               .into(imageViewDetailFragmentSavedId)

            textViewDetailFragmentCookingTimeId.text = resources.getString(R.string.estimate_cooking_time,dish.cookingTime)
        }
    }


}