package br.com.raveline.reciperx.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.databinding.ActivityNewDishBinding

class NewDishActivity : AppCompatActivity() {
    private lateinit var newDishBinding: ActivityNewDishBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newDishBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_dish)
        setContentView(newDishBinding.root)
    }
}