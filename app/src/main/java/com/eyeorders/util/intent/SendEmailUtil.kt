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
class SendEmailUtil @Inject constructor(@ActivityContext private val context: Context) {

    fun sendEmail(recipientEmail: String, subject: String = "", body: String = "") {
        val intent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", recipientEmail, null
            )
        )
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.send_email_hint)
            )
        )
    }
}
