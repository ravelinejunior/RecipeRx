package br.com.raveline.reciperx.listeners.background_workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        return try {

            Log.i("NotificationWorker", "Work: ${Date().time}")
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}