package br.com.raveline.reciperx.view.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import br.com.raveline.reciperx.DishApplication
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.data.model.DishModel
import br.com.raveline.reciperx.databinding.ActivityNewDishBinding
import br.com.raveline.reciperx.databinding.DialogCustomSelectImageBinding
import br.com.raveline.reciperx.databinding.DialogCustomSpinnerDataBinding
import br.com.raveline.reciperx.utils.Constants.DISH_CATEGORY
import br.com.raveline.reciperx.utils.Constants.DISH_COOKING_TYPE
import br.com.raveline.reciperx.utils.Constants.DISH_IMAGE_SOURCE_LOCAL
import br.com.raveline.reciperx.utils.Constants.DISH_TYPE
import br.com.raveline.reciperx.utils.Constants.cameraIdKey
import br.com.raveline.reciperx.utils.Constants.cameraNameKey
import br.com.raveline.reciperx.utils.Constants.dishCategories
import br.com.raveline.reciperx.utils.Constants.dishCookingTime
import br.com.raveline.reciperx.utils.Constants.dishTypes
import br.com.raveline.reciperx.utils.Constants.galleryIdKey
import br.com.raveline.reciperx.view.adapter.CustomSpinnerAdapter
import br.com.raveline.reciperx.viewmodel.FavDishViewModel
import br.com.raveline.reciperx.viewmodel.factories.FavDishViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class NewDishActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var customDialog: Dialog
    private lateinit var newDishBinding: ActivityNewDishBinding
    private var mImagePath = ""

    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as DishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newDishBinding = ActivityNewDishBinding.inflate(layoutInflater)
        setContentView(newDishBinding.root)
        setupActionBar()

        newDishBinding.imageViewNewDishAddNewImageId.setOnClickListener(this)
        newDishBinding.imageViewNewDishNoImageId.setOnClickListener(this)
        newDishBinding.textInputEditTextCategoryId.setOnClickListener(this)
        newDishBinding.textInputEditTextTypeId.setOnClickListener(this)
        newDishBinding.textInputEditTextCookingTimeId.setOnClickListener(this)
        newDishBinding.buttonNewDishAddNewDishId.setOnClickListener(this)
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        val dBinding = DialogCustomSelectImageBinding.inflate(layoutInflater)
        dialog.setContentView(dBinding.root)
        dialog.show()

        dBinding.apply {

            //Camera
            textViewCustomDialogImageCameraId.setOnClickListener {
                getCameraPermissions()
                dialog.dismiss()
            }

            imageViewCustomDialogImageCameraId.setOnClickListener {
                getCameraPermissions()
                dialog.dismiss()
            }

            //Gallery
            textViewCustomDialogImageGalleryId.setOnClickListener {
                getGalleryPermissions()
                dialog.dismiss()
            }

            imageViewCustomDialogImageGalleryId.setOnClickListener {
                getGalleryPermissions()
                dialog.dismiss()
            }

        }
    }

    fun selectedListItem(item: String, selection: String) {
        when (selection) {
            DISH_TYPE -> {
                customDialog.dismiss()
                newDishBinding.textInputEditTextTypeId.setText(item)
            }
            DISH_CATEGORY -> {
                customDialog.dismiss()
                newDishBinding.textInputEditTextCategoryId.setText(item)
            }
            DISH_COOKING_TYPE -> {
                customDialog.dismiss()
                newDishBinding.textInputEditTextCookingTimeId.setText(item)
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(cameraNameKey, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (ioE: IOException) {
            ioE.printStackTrace()
        }

        return file.absolutePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when {
                requestCode == cameraIdKey -> {
                    data?.extras?.let {
                        val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                        displayImageFromCamera(thumbnail)
                    }
                }
                requestCode == galleryIdKey -> {
                    if (data != null && data.data != null) {
                        val selectedPhotoUri = data.data
                        displayImageFromGallery(selectedPhotoUri)
                    } else {
                        Snackbar.make(
                            newDishBinding.root.rootView,
                            resources.getString(androidx.compose.ui.R.string.default_error_message),
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }

                }
                resultCode == Activity.RESULT_CANCELED -> {
                    Snackbar.make(newDishBinding.root.rootView, "Cancelled", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun getGalleryPermissions() {
        Dexter.withContext(applicationContext).withPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(permission: PermissionGrantedResponse?) {
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(galleryIntent, galleryIdKey)
            }

            override fun onPermissionDenied(pDenied: PermissionDeniedResponse?) {
                Snackbar.make(
                    newDishBinding.root.rootView,
                    "Permissions Denied!",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }

            override fun onPermissionRationaleShouldBeShown(
                pRequest: PermissionRequest?,
                pToken: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }

        }).onSameThread().check()
    }

    private fun getCameraPermissions() {
        Dexter.withContext(applicationContext).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    if (report.areAllPermissionsGranted()) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, cameraIdKey)
                    } else {
                        showRationalDialogForPermissions()
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                requests: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this@NewDishActivity)
            .setMessage("To use this functionality you must grant all the required permissions.")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun customItemsDialog(mTitle: String, items: List<String>, selected: String) {
        customDialog = Dialog(this)
        val dialogBinding: DialogCustomSpinnerDataBinding =
            DialogCustomSpinnerDataBinding.inflate(layoutInflater)
        val customAdapter = CustomSpinnerAdapter(this, selected, items)
        customDialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            textViewCustomSpinnerId.text = mTitle
            recyclerViewCustomSpinnerId.adapter = customAdapter
        }

        customDialog.show()

    }

    private fun setupActionBar() {
        setSupportActionBar(newDishBinding.toolbarNewDishId)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        newDishBinding.apply {
            toolbarNewDishId.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }


    private fun displayImageFromCamera(bitmap: Bitmap) {
        newDishBinding.apply {

            Glide.with(applicationContext).load(
                bitmap
            )
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.giphy
                    )
                ).into(imageViewNewDishNoImageId)

            Glide.with(applicationContext).load(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.ic_edit_white
                )
            )
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.giphy
                    )
                ).into(newDishBinding.imageViewNewDishAddNewImageId)

            frameLayoutNewDishId.setBackgroundColor(resources.getColor(R.color.white))

            mImagePath = saveImageToInternalStorage(bitmap)

        }
    }

    private fun displayImageFromGallery(selectedPhotoUri: Uri?) {
        Glide.with(applicationContext).load(selectedPhotoUri)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.giphy
                )
            ).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Toast.makeText(
                        applicationContext,
                        e?.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let {
                        val bitmap: Bitmap = resource.toBitmap()
                        mImagePath = saveImageToInternalStorage(bitmap)
                    }

                    return false
                }

            })
            .into(newDishBinding.imageViewNewDishNoImageId)

        Glide.with(applicationContext).load(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.ic_edit_white
            )
        )
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.giphy
                )
            ).into(newDishBinding.imageViewNewDishAddNewImageId)

        newDishBinding.frameLayoutNewDishId.setBackgroundColor(resources.getColor(R.color.white))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageViewNewDishAddNewImageId,
            R.id.imageViewNewDishNoImageId -> {
                showCustomDialog()
            }
            R.id.textInputEditTextCookingTimeId -> customItemsDialog(
                resources.getString(R.string.cooking_time_minutes_dish),
                dishCookingTime(),
                DISH_COOKING_TYPE
            )
            R.id.textInputEditTextTypeId -> customItemsDialog(
                resources.getString(R.string.dish_type),
                dishTypes(),
                DISH_TYPE
            )
            R.id.textInputEditTextCategoryId -> customItemsDialog(
                resources.getString(R.string.category_dish),
                dishCategories(),
                DISH_CATEGORY
            )

            R.id.buttonNewDishAddNewDishId -> {
                val title =
                    newDishBinding.textInputEditTextNameId.text.toString().trim { it <= ' ' }
                val type = newDishBinding.textInputEditTextTypeId.text.toString().trim { it <= ' ' }
                val category =
                    newDishBinding.textInputEditTextCategoryId.text.toString().trim { it <= ' ' }
                val ingredients =
                    newDishBinding.textInputEditTextIngredientsId.text.toString().trim { it <= ' ' }
                val cookingTime =
                    newDishBinding.textInputEditTextCookingTimeId.text.toString().trim { it <= ' ' }
                val cookingDirection =
                    newDishBinding.textInputEditTextDirectionToCookId.text.toString()
                        .trim { it <= ' ' }

                when {
                    TextUtils.isEmpty(mImagePath) -> {
                        Snackbar.make(
                            newDishBinding.root.rootView,
                            resources.getString(R.string.error_image_selected),
                            Snackbar.LENGTH_SHORT
                        )
                            .setActionTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.red
                                )
                            )
                            .show()
                    }
                    TextUtils.isEmpty(title) -> {
                        Snackbar.make(
                            newDishBinding.root.rootView,
                            resources.getString(R.string.error_title_selected),
                            Snackbar.LENGTH_SHORT
                        )
                            .setActionTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.red
                                )
                            )
                            .show()
                    }
                    TextUtils.isEmpty(type) -> {
                        Snackbar.make(
                            newDishBinding.root.rootView,
                            resources.getString(R.string.error_type_selected),
                            Snackbar.LENGTH_SHORT
                        )
                            .setActionTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.red
                                )
                            )
                            .show()
                    }
                    TextUtils.isEmpty(category) -> {
                        Snackbar.make(
                            newDishBinding.root.rootView,
                            resources.getString(R.string.error_category_selected),
                            Snackbar.LENGTH_SHORT
                        )
                            .setActionTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.red
                                )
                            )
                            .show()
                    }
                    TextUtils.isEmpty(ingredients) -> {
                        Snackbar.make(
                            newDishBinding.root.rootView,
                            resources.getString(R.string.error_ingredients_selected),
                            Snackbar.LENGTH_SHORT
                        )
                            .setActionTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.red
                                )
                            )
                            .show()
                    }
                    TextUtils.isEmpty(cookingTime) -> {
                        Snackbar.make(
                            newDishBinding.root.rootView,
                            resources.getString(R.string.error_cooking_time_selected),
                            Snackbar.LENGTH_SHORT
                        )
                            .setActionTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.red
                                )
                            )
                            .show()
                    }

                    TextUtils.isEmpty(cookingDirection) -> {
                        Snackbar.make(
                            newDishBinding.root.rootView,
                            resources.getString(R.string.error_cooking_direction_selected),
                            Snackbar.LENGTH_SHORT
                        )
                            .setActionTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.red
                                )
                            )
                            .show()
                    }
                    else -> {
                        val dish = DishModel(
                            title = title,
                            category = category,
                            type = type,
                            cookingTime = cookingTime,
                            directionsToCook = cookingDirection,
                            ingredients = ingredients,
                            image = mImagePath,
                            imageSource = DISH_IMAGE_SOURCE_LOCAL
                        )

                        favDishViewModel.insert(dish)
                        Snackbar.make(
                            newDishBinding.root.rootView,
                            resources.getString(R.string.error_ingredients_selected),
                            Snackbar.LENGTH_SHORT
                        )
                            .setActionTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.teal_700
                                )
                            ).setBackgroundTint(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.white
                                )
                            )
                            .show()

                        finish()
                    }

                }
            }
        }
    }
}