package br.com.raveline.reciperx.view.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.databinding.ActivityNewDishBinding
import br.com.raveline.reciperx.databinding.DialogCustomSelectImageBinding

class NewDishActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var newDishBinding: ActivityNewDishBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newDishBinding = ActivityNewDishBinding.inflate(layoutInflater)
        setContentView(newDishBinding.root)
        setupActionBar()

        newDishBinding.imageViewNewDishAddNewImageId.setOnClickListener(this)
        newDishBinding.imageViewNewDishNoImageId.setOnClickListener(this)
    }

    private fun showCustomDialog(){
        val dialog = Dialog(this)
        val dBinding = DialogCustomSelectImageBinding.inflate(layoutInflater)
        dialog.setContentView(dBinding.root)
        dialog.show()

        dBinding.apply {

            //Camera
            textViewCustomDialogImageCameraId.setOnClickListener {
                Toast.makeText(this@NewDishActivity, "Camera Text", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            imageViewCustomDialogImageCameraId.setOnClickListener {
                Toast.makeText(this@NewDishActivity, "Camera Image", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            //Gallery
            textViewCustomDialogImageGalleryId.setOnClickListener {
                Toast.makeText(this@NewDishActivity, "Gallery Text", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            imageViewCustomDialogImageGalleryId.setOnClickListener {
                Toast.makeText(this@NewDishActivity, "Gallery Image", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        }
    }

    private fun setupActionBar(){
        setSupportActionBar(newDishBinding.toolbarNewDishId)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        newDishBinding.apply {
            toolbarNewDishId.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imageViewNewDishAddNewImageId,
            R.id.imageViewNewDishNoImageId ->{
                showCustomDialog()
            }
        }
    }
}