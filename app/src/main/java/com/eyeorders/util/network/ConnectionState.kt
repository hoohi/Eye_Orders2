/**
 * Author          : Muddassir Ahmed Khan
 * Contact         : muddassir.ahmed235@gmail.com
 * Github Username : muddassir235
 *
 * ConnectionChecker.kt
 *
 * Class used to check connectivity of the application to the internet. It works as follows
 *
 * - Every five seconds the app tries to ping google.com
 *    - If google.com loads within 2 seconds the app is marked as CONNECTED
 *    - If google.com takes more than 2 seconds to load the connection is deemed SLOW.
 *    - If google.com doesn't load the app is marked as DISCONNECTED
 */
package com.eyeorders.util.network

import com.eyeorders.data.dispatcher.AppDispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

enum class ConnectionState {
    CONNECTED, SLOW, DISCONNECTED
}

data class ConnectionResult(
    val failed: Boolean,
    val timeTaken: Long,
)

class ConnectionChecker @Inject constructor(
    private val dispatchers: AppDispatchers,
) {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(MAX_TIMEOUT_SECS, TimeUnit.SECONDS)
        .readTimeout(MAX_TIMEOUT_SECS, TimeUnit.SECONDS)
        .build()

    suspend fun evaluateConnection(onResult: ((connectionState: ConnectionState) -> Unit)) {
        val result = withContext(dispatchers.io) { pingUrl() }
        val connectionState = if (result.failed) {
            ConnectionState.DISCONNECTED
        } else {
            if (result.timeTaken > SLOW_CONNECTION_MIN_TIME) ConnectionState.SLOW else ConnectionState.CONNECTED
        }

        onResult.invoke(connectionState)
    }

    private suspend fun pingUrl(): ConnectionResult {
        val startTime = System.currentTimeMillis()
        val request = Request.Builder().url(URL).build()
        return suspendCoroutine { continuation ->
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWith(
                        Result.success(
                            ConnectionResult(
                                true, System.currentTimeMillis() - startTime
                            )
                        )
                    )
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resumeWith(
                        Result.success(
                            ConnectionResult(
                                false, System.currentTimeMillis() - startTime
                            )
                        )
                    )
                }
            })

        }
    }

    companion object {
        private const val URL = "https://www.google.com"
        private const val SLOW_CONNECTION_MIN_TIME = 2000
        private const val MAX_TIMEOUT_SECS = 60L
    }
}
