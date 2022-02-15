package br.com.raveline.reciperx

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.com.raveline.reciperx.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(mainBinding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMain) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.dashboardFragment,
                R.id.notificationFragment
            )
        )

        mainBinding.bnvMainId.setupWithNavController(navController)
        mainBinding.bnvMainId.itemIconTintList = null
        setupActionBarWithNavController(navController, appBarConfig)
    }

    fun hideBottomNavigationView() {
        mainBinding.apply {
            bnvMainId.clearAnimation()
            bnvMainId.animate().translationY(bnvMainId.height.toFloat()).duration = 300
        }.also {
            lifecycleScope.launch {
                delay(300)
                mainBinding.bnvMainId.visibility = GONE
            }
        }
    }

    fun showBottomNavigationView() {
        mainBinding.apply {
            bnvMainId.visibility = VISIBLE
            bnvMainId.clearAnimation()
            bnvMainId.animate().translationY(0f).duration = 600

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}