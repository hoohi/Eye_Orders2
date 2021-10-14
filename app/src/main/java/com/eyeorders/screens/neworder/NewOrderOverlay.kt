package com.eyeorders.screens.neworder

import android.content.Context
import android.content.Intent
import com.eyeorders.data.analytics.AnalyticEvent
import com.eyeorders.data.analytics.Analytics
import com.eyeorders.util.mediaplayer.SoundPlayer
import com.eyeorders.util.overlay.OverlayHelper
import com.eyeorders.util.overlay.OverlayManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewOrderOverlay @Inject constructor(
    private val analytics: Analytics,
    private val overlayManager: OverlayManager,
    private val soundPlayer: SoundPlayer,
    private val newOrderIntentCreator: NewOrderIntentCreator,
    @ApplicationContext private val context: Context,
) {

    private val layoutParams = OverlayHelper.getFullScreenLayoutParams()
    private val overlayView = NewOrderOverlayView(context)

    fun showOverlay(order: NewOrderItem) {
        if (OverlayHelper.hasOverlayPermission(context)) {
            Timber.d("Show overlay: $order")
            overlayView.setNewOrder(order)
            overlayView.onClick {
                soundPlayer.stop()
                hideOverlay()
                context.startActivity(
                    newOrderIntentCreator.createIntent(order.orderId).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
            }
            overlayManager.showOverlay(overlayView, layoutParams)
            analytics.logEvent(AnalyticEvent.ShowPopup)
        }
    }

    fun hideOverlay() {
        if (OverlayHelper.hasOverlayPermission(context)) {
            overlayManager.hideOverlay(overlayView)
        }
    }
}