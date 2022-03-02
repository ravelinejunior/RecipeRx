package br.com.raveline.reciperx.view.fragment.detail

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import br.com.raveline.reciperx.DishApplication
import br.com.raveline.reciperx.MainActivity
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.databinding.DishDetailFragmentBinding
import br.com.raveline.reciperx.utils.Constants
import br.com.raveline.reciperx.viewmodel.FavDishViewModel
import br.com.raveline.reciperx.viewmodel.factories.FavDishViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class DishDetailFragment : Fragment(), View.OnClickListener {

    private val favViewDishModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as DishApplication).repository)
    }
    private lateinit var dBinding: DishDetailFragmentBinding
    private val args: DishDetailFragmentArgs by navArgs()
    private lateinit var dish: DishModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dBinding = DishDetailFragmentBinding.inflate(inflater, container, false)
        dBinding.dish = args.dish
        dBinding.lifecycleOwner = this

        if (args.dish != null) {
            dish = args.dish!!
        }

        displayData(dish)

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

    private fun setFavoriteIconChange(isFromStart: Boolean) {
        if (dish.favoriteDish) {

            dBinding.imageViewDetailFragmentFavoriteId.setImageDrawable(
                getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_favorite_24
                )
            )

            if (!isFromStart) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_added_to_favorites),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            dBinding.imageViewDetailFragmentFavoriteId.setImageDrawable(
                getDrawable(
                    requireContext(),
                    R.drawable.ic_favorite_border_24
                )
            )

            if (!isFromStart) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_removed_from_favorites),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayData(dish: DishModel) {
        dBinding.apply {
            Glide.with(requireContext()).load(dish.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(
                    getDrawable(
                        requireContext(),
                        R.drawable.giphy
                    )
                ).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        /* if (resource != null) {
                             Palette.from(resource.toBitmap()).generate { palette ->
                                 val intColor =
                                     palette?.vibrantSwatch?.rgb ?: palette?.lightVibrantSwatch?.rgb
                                     ?: 0

                                 dBinding.relativeLayoutDetailFragmentId.setBackgroundColor(intColor)

                             }
                         }*/

                        return false
                    }
                })
                .into(imageViewDetailFragmentSavedId)

            textViewDetailFragmentCookingTimeId.text =
                resources.getString(R.string.estimate_cooking_time, dish.cookingTime)
            setFavoriteIconChange(true)
            imageViewDetailFragmentFavoriteId.setOnClickListener(this@DishDetailFragment)

            if (dish.isFromRandom) {
                linearLayoutDetailFragmentId.visibility = GONE
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.linearLayoutDetailFragmentId,
            R.id.imageViewDetailFragmentFavoriteId -> {
                dish.favoriteDish = !dish.favoriteDish
                favViewDishModel.updateDish(dish)
                setFavoriteIconChange(false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuShareItemId) {
            val type = "text/plain"
            val subject = "Check this recipe bro"
            var extraText = ""
            val shareWith = "Share with "

            dish.let {
                var image = ""
                if (it.imageSource == Constants.DISH_IMAGE_SOURCE_REMOTE) {
                    image = it.image
                }
                extraText =
                    "$image\n\nTitle:${it.title}\n\nType: ${it.type}\n\nCategory: ${it.category}\n\n" +
                            "Ingredients: ${it.ingredients}\n\nInstructions: ${it.directionsToCook}" +
                            "\n\n Time required to cook this is approximating ${it.cookingTime} minutes."
            }
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = type
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, extraText)
            startActivity(Intent.createChooser(intent, shareWith))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_share, menu)
    }


}