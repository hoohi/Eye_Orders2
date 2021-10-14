package com.eyeorders.data.remote.api.interceptor

import com.eyeorders.data.eventbus.EventBus
import com.eyeorders.data.eventbus.event.LogoutEvent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationErrorInterceptor @Inject constructor(
    private val eventBus: EventBus
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 401) {
            runBlocking {
                eventBus.produceEvent(LogoutEvent())
            }
        }
        return response
    }
}