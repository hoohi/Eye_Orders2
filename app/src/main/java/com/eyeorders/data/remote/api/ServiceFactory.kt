package com.eyeorders.data.remote.api

import com.eyeorders.data.remote.api.interceptor.AuthTokenInterceptor
import com.eyeorders.data.remote.api.interceptor.AuthorizationErrorInterceptor
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceFactory {

    private const val MAX_TIMEOUT_SECS = 60L
    private const val BASE_URL = "https://tasleem.in/api/frontend/web/"

    fun makeApiService(
        gson: Gson,
        isDebug: Boolean,
        authToken: AuthTokenInterceptor,
        authErrorInterceptor:AuthorizationErrorInterceptor
    ): ApiService {
        val okHttpClient = makeOkHttpClient(
            makeLoggingInterceptor((isDebug)),
            authToken,
            authErrorInterceptor
        )
        return makeApiService(okHttpClient, gson)
    }

    private fun makeApiService(client: OkHttpClient, gson: Gson): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(ApiService::class.java)
    }

    private fun makeOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authToken: AuthTokenInterceptor,
        authErrorInterceptor:AuthorizationErrorInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authErrorInterceptor)
            .addInterceptor(authToken)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(MAX_TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(MAX_TIMEOUT_SECS, TimeUnit.SECONDS)
            .build()
    }

    private fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (isDebug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }
}
