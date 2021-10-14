package com.eyeorders.neworderschecker

import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.model.order.OrderResponse
import com.eyeorders.screens.neworder.NewOrderItem
import com.eyeorders.screens.neworder.NewOrderOverlay
import com.eyeorders.util.StringResource
import com.eyeorders.util.mediaplayer.NoInternetSoundPlayer
import com.eyeorders.util.mediaplayer.SoundPlayer
import com.eyeorders.util.notification.AppNotificationHelper
import com.eyeorders.util.notification.NotificationData
import com.tasleem.orders.R
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewOrderNotifier @Inject constructor(
    private val workingHoursChecker: WorkingHoursChecker,
    private val timeChecker: TimeChecker,
    private val newOrderOverlay: NewOrderOverlay,
    private val notificationHelper: AppNotificationHelper,
    private val soundPlayer: SoundPlayer,
    private val noInternetSoundPlayer: NoInternetSoundPlayer,
    private val dispatchers: AppDispatchers,
    private val stringResource: StringResource,
) {

    suspend fun showPopupIfWithinTime(order: OrderResponse) {
        if (timeChecker.isWithinTime()) {
            withContext(dispatchers.main) {
                soundPlayer.play()
                val count = order.products.size
                newOrderOverlay.showOverlay(NewOrderItem(order.id, count))
                notificationHelper.showOrderNotification(
                    NotificationData(
                        stringResource.getString(
                            R.string.new_order_notification_title
                        ),
                        stringResource.getString(
                            R.string.new_order_notification_msg,
                            order.customer
                        ),
                        order.id,
                        count
                    )
                )
            }
        }
    }

    suspend fun playAlert() {
        if (workingHoursChecker.isWithinWorkHoursToday()) {
            noInternetSoundPlayer.play()
        }
    }

    suspend fun stopAlert() {
        if (workingHoursChecker.isWithinWorkHoursToday()) {
            noInternetSoundPlayer.stop()
        }
    }
}