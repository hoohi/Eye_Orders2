package com.eyeorders.data.analytics

import com.eyeorders.data.analytics.google.getBundleParams
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.SCREEN_VIEW
import javax.inject.Inject

class Analytics @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) {

    fun logEvent(event: AnalyticEvent) {
        if (event is AnalyticEvent.ScreenViewEvent) {
            firebaseAnalytics.logEvent(SCREEN_VIEW, event.getBundleParams())
        } else {
            firebaseAnalytics.logEvent(event.eventName, event.getBundleParams())
        }
    }

}