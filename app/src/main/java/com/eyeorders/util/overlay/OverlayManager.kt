package com.eyeorders.util.overlay

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.view.View
import android.view.WindowManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlayManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    private var showingOverlay = false

    fun showOverlay(overlayView: View, overlayParams: WindowManager.LayoutParams) {
        Timber.d("Try Show overlay")
        if (showingOverlay.not()) {
            Timber.d("Show overlay")
            showingOverlay = true
            windowManager.addView(overlayView, overlayParams)
        }
    }

    fun hideOverlay(overlayView: View) {
        if (showingOverlay) {
            showingOverlay = false
            windowManager.removeViewImmediate(overlayView)
        }
    }
}