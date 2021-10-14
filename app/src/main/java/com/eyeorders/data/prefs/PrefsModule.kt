package com.eyeorders.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PrefsModule {

    @Binds
    @Singleton
    fun bindPrefs(dataManagerImpl: DataManagerImpl): PrefsDataManager

    companion object {
        private const val PREFS_NAME = "eyezz"
        private const val DATA_STORE_NAME = "eyezz_store"

        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

        @Provides
        @Singleton
        fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return context.dataStore
        }
    }
}