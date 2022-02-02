package br.com.raveline.reciperx.utils

import android.app.*
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.raveline.reciperx.MainActivity
import br.com.raveline.reciperx.R
import br.com.raveline.reciperx.utils.Constants.channelIdKey
import br.com.raveline.reciperx.utils.Constants.notificationIdKey

class NotificationHelper(val context: Context) {

    fun createNotification() {
        createNotificationChannel()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_ONE_SHOT)
        val icon = BitmapFactory.decodeResource(context.resources, androidx.databinding.library.baseAdapters.R.drawable.abc_btn_check_material)

        val notification = NotificationCompat.Builder(context, channelIdKey)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(icon)
            .setContentTitle("Hey, pay attention!")
            .setContentText("New message")
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(icon)
                    .bigLargeIcon(null)
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(notificationIdKey, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelIdKey
            val descriptionText = "Channel_Description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelIdKey, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)


        }
    }


}