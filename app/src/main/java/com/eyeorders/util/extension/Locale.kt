@file:Suppress("DEPRECATION")

package com.eyeorders.util.extension

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.View
import java.util.Locale


@SuppressLint("ObsoleteSdkInt")
fun setLocale(activity: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources = activity.resources
    val config: Configuration = resources.configuration
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        config.setLocale(locale)
    } else {
        config.locale = locale
    }
    resources.updateConfiguration(config, resources.displayMetrics)
}

@SuppressLint("ObsoleteSdkInt")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
 fun Activity.forceRTLIfSupported() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }
}
