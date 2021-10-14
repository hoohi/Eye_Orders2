package com.eyeorders.data.remote.api.interceptor

import com.eyeorders.data.prefs.PrefsDataManager
import kotlinx.coroutines.runBlocking
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.*
import javax.inject.Inject


class AuthTokenInterceptor @Inject constructor(
    private val prefsDataManager: PrefsDataManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = addHeaderFields(request)
        request = modifyRequestBody(request)
        return chain.proceed(request)
    }

    private fun addHeaderFields(request: Request): Request {
        return request.newBuilder()
            .addHeader("timestamp", Date().time.toString())
            .addHeader("os", "android")
            .build()
    }

    @Suppress("NAME_SHADOWING")
    private fun modifyRequestBody(request: Request): Request {
        var request = request
        if ("POST" == request.method) {
            if (request.body is FormBody) {
                val bodyBuilder = FormBody.Builder()
                var formBody = request.body as FormBody

                for (i in 0 until formBody.size) {
                    bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i))
                }

                val token = runBlocking {
                    prefsDataManager.getAuthToken()
                }

                formBody = bodyBuilder
                    .addEncoded(KEY, token)
                    .build()
                request = request.newBuilder().post(formBody).build()
            }
        }
        return request
    }

    companion object {
        private const val KEY = "apiToken"
    }
}