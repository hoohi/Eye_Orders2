package com.eyeorders.screens.neworder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.util.mediaplayer.SoundPlayer
import com.eyeorders.util.notification.AppNotificationHelper
import com.eyeorders.util.notification.NotificationData
import com.tasleem.orders.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@HiltWorker
class TestNewOrdersWorker @AssistedInject constructor(
    private val notificationHelper: AppNotificationHelper,
    private val soundPlayer: SoundPlayer,
    private val dispatchers: AppDispatchers,
    private val newOrderOverlay: NewOrderOverlay,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.d("Starting work...: $soundPlayer")
        return try {
            withContext(dispatchers.main) {
                val orderId = 32782
                val orderCount = Random.nextInt(10) + 1
                soundPlayer.play()
                newOrderOverlay.showOverlay(NewOrderItem(orderId, orderCount))
                notificationHelper.showOrderNotification(
                    NotificationData(
                        applicationContext.getString(
                            R.string.new_order_notification_title
                        ),
                        applicationContext.getString(R.string.new_order_notification_msg_tasleem),
                        orderId,
                        orderCount
                    )
                )
            }
            Result.success()
        } catch (e: Exception) {
            Timber.d("Work failed with: $e")
            Result.failure()
        }
    }


    companion object {
        private const val WORK_TAG = "TestNewOrdersWorker"
        fun scheduleWork(context: Context): UUID {
            Timber.d("scheduling TestNewOrdersWorker...")

            val workRequest = OneTimeWorkRequestBuilder<TestNewOrdersWorker>()
                .setInitialDelay(5, TimeUnit.SECONDS)
                .addTag(WORK_TAG)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_TAG, ExistingWorkPolicy.REPLACE, workRequest)
            return workRequest.id
        }
    }
}