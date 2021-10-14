package com.eyeorders.screens.neworder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.eyeorders.screens.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class NewOrderIntentCreator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun createIntent(orderId: Int): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(DOMAIN.plus(orderId))).apply {
            component = ComponentName(context, MainActivity::class.java)
        }
    }

    companion object {
        const val DOMAIN = "http://www.tasleem.in/orders/"
    }
}