package com.eyeorders.data.cache

import android.app.Application
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DbModule {
    companion object {
        @Provides
        @Singleton
        fun appDatabase(application: Application): AppDatabase {
            return AppDatabase.getInstance(application)
        }

        @Provides
        @Singleton
        fun workerManager(application: Application): WorkManager {
            return WorkManager.getInstance(application)
        }

    }
}