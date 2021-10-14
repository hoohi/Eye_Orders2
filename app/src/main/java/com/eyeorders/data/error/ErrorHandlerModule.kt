package com.eyeorders.data.error

import com.eyeorders.data.error.ErrorHandler
import com.eyeorders.data.error.ErrorHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ErrorHandlerModule {
    @Binds
    @Singleton
    fun bindAppDispatchers(errorHandlerImpl: ErrorHandlerImpl): ErrorHandler
}
