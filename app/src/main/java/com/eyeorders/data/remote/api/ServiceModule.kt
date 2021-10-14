package com.eyeorders.data.remote.api

import com.eyeorders.data.remote.api.deserialiser.BooleanTypeAdapter
import com.eyeorders.data.remote.api.deserialiser.DoubleTypeAdapter
import com.eyeorders.data.remote.api.deserialiser.IntTypeAdapter
import com.eyeorders.data.remote.api.deserialiser.LongTypeAdapter
import com.eyeorders.data.remote.api.interceptor.AuthTokenInterceptor
import com.eyeorders.data.remote.api.interceptor.AuthorizationErrorInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tasleem.orders.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ServiceModule {

    companion object {
        @Provides
        @Singleton
        fun provideGson(): Gson {
            return GsonBuilder()
                .registerTypeAdapter(
                    Boolean::class.java,
                    BooleanTypeAdapter()
                )
                .registerTypeAdapter(
                    Double::class.java,
                    DoubleTypeAdapter()
                ).registerTypeAdapter(
                    Int::class.java,
                    IntTypeAdapter()
                )
                .registerTypeAdapter(
                    Long::class.java,
                    LongTypeAdapter()
                )
                .create()
        }

        @Provides
        @Singleton
        fun provideCarApiServiceService(
            gson: Gson,
            authorizationErrorInterceptor: AuthorizationErrorInterceptor,
            authTokenInterceptor: AuthTokenInterceptor
        ): ApiService {
            return ServiceFactory.makeApiService(
                gson,
                BuildConfig.DEBUG,
                authTokenInterceptor,
                authorizationErrorInterceptor
            )
        }
    }
}