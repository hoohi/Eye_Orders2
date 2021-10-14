package com.eyeorders.data.prefs

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.eyeorders.data.dispatcher.AppDispatchers
import com.eyeorders.data.language.LanguageChangeObserver.Companion.LANGUAGE
import com.eyeorders.data.model.menu.VendorCategory
import com.eyeorders.data.model.user.UserRetailer
import com.eyeorders.data.orderstats.OrderStats
import com.eyeorders.data.remote.api.response.WorkHoursResponse
import com.eyeorders.screens.storestatus.StoreStatus
import com.eyeorders.util.language.LanguageHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataManagerImpl @Inject constructor(
    private val appDispatchers: AppDispatchers,
    private val gson: Gson,
    private val dataStore: DataStore<Preferences>,
    sharedPreferences: SharedPreferences
) : BasePreferencesManager(sharedPreferences), PrefsDataManager {

    override suspend fun loggedIn(): Boolean {
        return withContext(appDispatchers.io) {
            getBooleanPreference(LOGGED_IN, false)
        }
    }

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        withContext(appDispatchers.io) {
            setBooleanPreference(LOGGED_IN, loggedIn)
        }
    }

    override suspend fun getAuthToken(): String {
        return withContext(appDispatchers.io) {
            getStringPreference(AUTH_TOKEN, "")
        }
    }

    override suspend fun setAuthToken(token: String) {
        withContext(appDispatchers.io) {
            setStringPreference(AUTH_TOKEN, token)
        }
    }

    override suspend fun setPushToken(token: String) {
        withContext(appDispatchers.io) {
            setStringPreference(PUSH_TOKEN, token)
        }
    }

    override suspend fun getPushToken(): String {
        return withContext(appDispatchers.io) {
            getStringPreference(PUSH_TOKEN, "")
        }
    }

    override suspend fun getPushChannelId(): String {
        return withContext(appDispatchers.io) {
            getStringPreference(PUSH_CHANNEL_ID, "")
        }
    }

    override suspend fun setPushChannelId(token: String) {
        withContext(appDispatchers.io) {
            setStringPreference(PUSH_CHANNEL_ID, token)
        }
    }

    override suspend fun getLanguage(): String {
        return withContext(appDispatchers.io) {
            getStringPreference(LANGUAGE, LanguageHelper.LANGUAGE_ENGLISH)
        }
    }

    override suspend fun setLanguage(lang: String) {
        withContext(appDispatchers.io) {
            setStringPreference(LANGUAGE, lang)
        }
    }

    override suspend fun setStoreStatus(status: StoreStatus) {
        dataStore.edit {
            it[STORE_OPEN_KEY] = status.ordinal
        }
    }

    override fun storeStatus(): Flow<StoreStatus> {
        return dataStore.data.map {
            val statusOrdinal = it[STORE_OPEN_KEY] ?: StoreStatus.BUSY.ordinal
            StoreStatus.values()[statusOrdinal]
        }
    }

    override suspend fun getUser(): UserRetailer {
        return withContext(appDispatchers.io) {
            gson.fromJson(getStringPreference(USER), UserRetailer::class.java)
        }
    }


    override suspend fun saveUser(user: UserRetailer) {
        withContext(appDispatchers.io) {
            setStringPreference(USER, gson.toJson(user))
        }
    }

    override suspend fun getVendorCategories(): List<VendorCategory> {
        return withContext(appDispatchers.io) {
            gson.fromJson(
                getStringPreference(CATEGORIES, "[]"),
                object : TypeToken<List<VendorCategory>>() {}.type
            )
        }
    }

    override suspend fun saveVendorCategories(categories: List<VendorCategory>) {
        withContext(appDispatchers.io) {
            setStringPreference(CATEGORIES, gson.toJson(categories))
        }
    }

    override suspend fun getOrderStats(): OrderStats {
        return withContext(appDispatchers.io) {
            gson.fromJson(getStringPreference(OrderStats.KEY), OrderStats::class.java)
        }
    }

    override suspend fun saveOrderStats(orderStats: OrderStats) {
        withContext(appDispatchers.io) {
            setStringPreference(OrderStats.KEY, gson.toJson(orderStats))
        }
    }

    override suspend fun clearAll() {
        withContext(appDispatchers.io) {
            sharedPreferences.edit().clear().commit()
        }
    }

    override suspend fun setUpdatedOrderStatus(updated: Boolean) {
        withContext(appDispatchers.io) {
            setBooleanPreference(UPDATED_ORDER_STATUS, updated)
        }
    }

    override suspend fun updatedOrderStatus(): Boolean {
        return withContext(appDispatchers.io) {
            getBooleanPreference(UPDATED_ORDER_STATUS, false)
        }
    }

    override suspend fun getWorkHours(): WorkHoursResponse? {
        return withContext(appDispatchers.io) {
            try {
                gson.fromJson(getStringPreference(WORK_HOURS), WorkHoursResponse::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun saveWorkHours(workHours: WorkHoursResponse) {
        withContext(appDispatchers.io) {
            setStringPreference(WORK_HOURS, gson.toJson(workHours))
        }
    }

    companion object {
        private const val LOGGED_IN = "LOGGED_IN"
        private const val AUTH_TOKEN = "TOKEN"
        private const val PUSH_TOKEN = "PUSH_TOKEN"
        private const val PUSH_CHANNEL_ID = "PUSH_CHANNEL_ID"
        private const val STORE_OPEN = "STORE_OPEN"
        private val STORE_OPEN_KEY = intPreferencesKey(STORE_OPEN)
        private const val USER = "USER"
        private const val CATEGORIES = "CATEGORIES"
        private const val UPDATED_ORDER_STATUS = "UPDATED_ORDER_STATUS"
        private const val WORK_HOURS = "WORK_HOURS"
    }
}