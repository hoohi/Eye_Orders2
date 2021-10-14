package com.eyeorders.util.intent

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.tasleem.orders.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


/**
 * Created by kryptkode on 2/26/2020.
 */
class DialPhoneUtil @Inject constructor(@ActivityContext private val context: Context) {

    fun dialPhone(phoneNumber: String) {
        val intent = Intent(
            Intent.ACTION_DIAL, Uri.fromParts(
                "tel", phoneNumber, null
            )
        )
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.dial_phone_hint)
            )
        )
    }
}
