package br.com.raveline.reciperx.view.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.databinding.ActivityNewDishBinding
import br.com.raveline.reciperx.databinding.DialogCustomSelectImageBinding
import br.com.raveline.reciperx.utils.Constants
import br.com.raveline.reciperx.utils.Constants.cameraIdKey
import br.com.raveline.reciperx.utils.SystemFunctions.getImageUri
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class NewDishActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var newDishBinding: ActivityNewDishBinding
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == cameraIdKey) {
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                    newDishBinding.apply {
                        imageViewNewDishNoImageId.setImageBitmap(thumbnail)
                        imageViewNewDishAddNewImageId.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@NewDishActivity,
                                R.drawable.ic_edit_white
                            )
                        )
                    }

                }
            }
        }
    }

    private fun getGalleryPermissions() {
        Dexter.withContext(this@NewDishActivity).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(
                            this@NewDishActivity,
                            "Gallery permission granted!",
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun getCameraPermissions() {
        Dexter.withContext(this@NewDishActivity).withPermissions(
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