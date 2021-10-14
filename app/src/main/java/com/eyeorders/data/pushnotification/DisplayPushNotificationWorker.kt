package com.eyeorders.data.pushnotification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.screens.neworder.NewOrderItem
import com.eyeorders.screens.neworder.NewOrderOverlay
import com.eyeorders.util.mediaplayer.SoundPlayer
import com.eyeorders.util.notification.AppNotificationHelper
import com.eyeorders.util.notification.NotificationData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

@HiltWorker
class DisplayPushNotificationWorker @AssistedInject constructor(
    private val notificationHelper: AppNotificationHelper,
    private val soundPlayer: SoundPlayer,
    private val dispatchers: AppDispatchers,
    private val newOrderOverlay: NewOrderOverlay,
    private val prefsDataManager: PrefsDataManager,
    private val notificationParser: NotificationParser,
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.d("Starting work...")
        return try {
            val input = inputData.getString(WORK_DATA_KEY)
            Timber.d("Launnched coroutine: $input")
            val loggedIn = prefsDataManager.loggedIn()
            Timber.d("Launnched coroutine: $loggedIn")
            val userId = prefsDataManager.getUserId()
            Timber.d("User: $userId")
            val data = notificationParser.getNotificationExtras(input)
            Timber.d("data: $data")
            if (loggedIn && data?.vendorId == userId) {
                withContext(dispatchers.main) {
                    if (data.type == NotificationType.TYPE_DEFAULT) {
                        newOrderOverlay.showOverlay(NewOrderItem(data.orderID, data.numOfItems))
                        soundPlayer.play()
                        notificationHelper.showOrderNotification(
                            NotificationData(
                                data.title,
                                data.msg,
                                data.orderID,
                                data.numOfItems
                            )
                        )
                    } else {
                        notificationHelper.showNotification(
                            NotificationData(
                                data.title,
                                data.msg,
                            )
                        )
                    }

                }
            } else {
                Timber.d("User not logged in, ignoring notification")
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Work failed with")
            Result.failure()
        }
    }

    companion object {
        private const val WORK_TAG = "DisplayFcmNotificationWorker"
        private const val WORK_DATA_KEY = "DisplayFcmNotificationWorker_Key"
        fun scheduleWork(context: Context, data: String?): UUID {
            Timber.d("scheduling DisplayFcmNotificationWorker...")
            val inputData = workDataOf(WORK_DATA_KEY to data)
            val workRequest = OneTimeWorkRequestBuilder<DisplayPushNotificationWorker>()
                .addTag(WORK_TAG)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_TAG, ExistingWorkPolicy.REPLACE, workRequest)
            Timber.d("scheduled DisplayFcmNotificationWorker: ${workRequest.id}")
            return workRequest.id
        }
    }
}