package br.com.raveline.reciperx

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import br.com.raveline.reciperx.databinding.ActivityMainBinding
import br.com.raveline.reciperx.listeners.background_workers.NotificationWorker
import br.com.raveline.reciperx.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

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
                R.id.randomFragment
            )
        )

        mainBinding.bnvMainId.setupWithNavController(navController)
        mainBinding.bnvMainId.itemIconTintList = null
        setupActionBarWithNavController(navController, appBarConfig)

        if(intent.hasExtra(Constants.notificationIdTitle)){
            val notificationId = intent.getIntExtra(Constants.notificationIdTitle,0)
            mainBinding.bnvMainId.selectedItemId = notificationId
        }

        startNotificationWork()

    }

    private fun startNotificationWork() = WorkManager.getInstance(this)
        .enqueueUniquePeriodicWork(
            getString(R.string.periodic_notification_work_msg),
            ExistingPeriodicWorkPolicy.KEEP,
            createNotificationWorkRequest()
        )

    private fun createNotificationWorkRequest() =
        PeriodicWorkRequestBuilder<NotificationWorker>(
            8, TimeUnit.HOURS
        ).setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.MINUTES)
            .setConstraints(createConstraints())
            .build()

    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(false)
        .setRequiresCharging(false)
        .build()

    fun hideBottomNavigationView() {
        mainBinding.apply {
            bnvMainId.clearAnimation()
            bnvMainId.animate().translationY(bnvMainId.height.toFloat()).duration = 600
        }.also {
            lifecycleScope.launch {
                delay(600)
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