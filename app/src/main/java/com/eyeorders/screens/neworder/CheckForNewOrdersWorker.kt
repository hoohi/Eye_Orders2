package com.eyeorders.screens.neworder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.model.DataState
import com.eyeorders.data.usecase.orders.GetNewOrdersUseCase
import com.eyeorders.util.mediaplayer.SoundPlayer
import com.eyeorders.util.notification.AppNotificationHelper
import com.eyeorders.util.notification.NotificationData
import com.tasleem.orders.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID

@HiltWorker
class CheckForNewOrdersWorker @AssistedInject constructor(
    private val getNewOrdersUseCase: GetNewOrdersUseCase,
    private val notificationHelper: AppNotificationHelper,
    private val soundPlayer: SoundPlayer,
    private val dispatchers: AppDispatchers,
    private val newOrderOverlay: NewOrderOverlay,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.d("Starting work...")
        return try {
            when (val result = getNewOrdersUseCase.execute()) {
                is DataState.Success -> {
                    Timber.d("Work is successful")
                    if (result.data.isNotEmpty()) {
                        val order = result.data.first()
                        val count = result.data.size
                        soundPlayer.play()
                        withContext(dispatchers.main){
                            newOrderOverlay.showOverlay(NewOrderItem(order.id, count))
                            notificationHelper.showOrderNotification(
                                NotificationData(
                                    applicationContext.getString(
                                        R.string.new_order_notification_title
                                    ),
                                    applicationContext.getString(
                                        R.string.new_order_notification_msg,
                                        order.customer
                                    ),
                                    order.id,
                                    order.products.size
                                )
                            )
                        }
                    }
                    Result.success()
                }

                is DataState.Error -> {
                    Timber.d("Work failed with: ${result.message}")
                    Result.failure()
                }
                else -> {
                    throw IllegalStateException("Should not emit $result state")
                }
            }
        } catch (e: Exception) {
            Timber.e("Work failed with: $e")
            Result.failure()
        }
    }


    companion object {
        private const val WORK_TAG = "work_tag"
        fun scheduleWork(context: Context): UUID {
            Timber.d("scheduling worker...")

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<CheckForNewOrdersWorker>()
                .setConstraints(constraints)
                .addTag(WORK_TAG)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_TAG, ExistingWorkPolicy.REPLACE, workRequest)
            return workRequest.id
        }
    }
}