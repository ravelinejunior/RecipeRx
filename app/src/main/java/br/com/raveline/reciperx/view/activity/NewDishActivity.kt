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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.databinding.ActivityNewDishBinding
import br.com.raveline.reciperx.databinding.DialogCustomSelectImageBinding
import br.com.raveline.reciperx.utils.Constants.cameraIdKey
import br.com.raveline.reciperx.utils.Constants.cameraNameKey
import br.com.raveline.reciperx.utils.Constants.galleryIdKey
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
    private lateinit var newDishBinding: ActivityNewDishBinding
    private var mImagePath = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newDishBinding = ActivityNewDishBinding.inflate(layoutInflater)
        setContentView(newDishBinding.root)
        setupActionBar()

        newDishBinding.imageViewNewDishAddNewImageId.setOnClickListener(this)
        newDishBinding.imageViewNewDishNoImageId.setOnClickListener(this)
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
            if (requestCode == cameraIdKey) {
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                    newDishBinding.apply {

                        Glide.with(applicationContext).load(
                            thumbnail
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

                        mImagePath = saveImageToInternalStorage(thumbnail)

                    }
                }
            } else if (requestCode == galleryIdKey) {
                data?.extras?.let {
                    val selectedPhotoUri = data.data
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
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(newDishBinding.root.rootView, "Cancelled", Snackbar.LENGTH_SHORT)
                    .show()
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

    private fun setupActionBar() {
        setSupportActionBar(newDishBinding.toolbarNewDishId)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        newDishBinding.apply {
            toolbarNewDishId.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageViewNewDishAddNewImageId,
            R.id.imageViewNewDishNoImageId -> {
                showCustomDialog()
            }
        }
    }
}