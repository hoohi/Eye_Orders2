package com.eyeorders.data.error

import com.eyeorders.data.analytics.AnalyticEvent.ServerErrorEvent
import com.eyeorders.data.analytics.Analytics
import com.eyeorders.data.exception.ServerException
import com.eyeorders.util.StringResource
import com.google.gson.Gson
import com.tasleem.orders.R
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class ErrorHandlerImpl @Inject constructor(
    private val stringResource: StringResource,
    private val analytics: Analytics,
    private val gson: Gson,
) :
    ErrorHandler {

    override fun getErrorMessage(e: Throwable?): String {
        if (e is ConnectException) {
            Timber.w(e, "Error occurred:")
            return getString(R.string.connect_exception)
        }
        if (e is UnknownHostException) {
            Timber.w(e, "Error occurred:")
            return getString(R.string.unknown_host_exception)
        }
        if (e is SocketTimeoutException) {
            Timber.w(e, "Error occurred:")
            return getString(R.string.timed_out_exception)
        }

        if (e is ServerException) {
            Timber.e(e, "Server Error occurred:")
            analytics.logEvent(ServerErrorEvent(gson.toJson(e.data)))
            return e.message ?: getString(R.string.unknown_exception)
        }

        return getString(R.string.unknown_exception)
    }

    private fun getString(resId: Int): String {
        return stringResource.getString(resId)
    }
}
