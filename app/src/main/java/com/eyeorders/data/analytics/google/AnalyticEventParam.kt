package com.eyeorders.data.analytics.google

import android.os.Bundle
import androidx.core.os.bundleOf
import com.eyeorders.data.analytics.AnalyticEvent
import com.google.firebase.analytics.FirebaseAnalytics

fun AnalyticEvent?.getBundleParams(): Bundle? {
    if (this == null) {
        return null
    }

    val bundle = Bundle()
    parameters?.forEach {
        when (val value = it.value) {
            is Int -> bundle.putInt(it.key, value)
            is Long -> bundle.putLong(it.key, it.value as Long)
            is Float -> bundle.putFloat(it.key, it.value as Float)
            is Double -> bundle.putDouble(it.key, it.value as Double)
            is Boolean -> bundle.putBoolean(it.key, it.value as Boolean)
            is String -> bundle.putString(it.key, it.value as String)
            else -> throw IllegalStateException("Cannot convert ${it.value.javaClass} to an accepted Bundle type")
        }
    }
    return bundle
}

fun AnalyticEvent.ScreenViewEvent.getBundleParams(): Bundle {
    return bundleOf(
        FirebaseAnalytics.Param.SCREEN_NAME to this.screenName,
        FirebaseAnalytics.Param.SCREEN_CLASS to this.screenClassName,
    )
}