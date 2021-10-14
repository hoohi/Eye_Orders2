package com.eyeorders.data.prefs

import com.eyeorders.data.model.menu.VendorCategory
import com.eyeorders.data.model.user.UserRetailer
import com.eyeorders.data.orderstats.OrderStats
import com.eyeorders.data.remote.api.response.WorkHoursResponse
import com.eyeorders.screens.storestatus.StoreStatus
import kotlinx.coroutines.flow.Flow


interface PrefsDataManager {
    suspend fun loggedIn(): Boolean
    suspend fun setLoggedIn(loggedIn: Boolean)

    suspend fun getAuthToken(): String
    suspend fun setAuthToken(token: String)

    suspend fun getPushToken(): String
    suspend fun setPushToken(token: String)

    suspend fun getPushChannelId(): String
    suspend fun setPushChannelId(token: String)

    suspend fun getLanguage(): String
    suspend fun setLanguage(lang: String)

    fun storeStatus(): Flow<StoreStatus>
    suspend fun setStoreStatus(status: StoreStatus)

    suspend fun saveUser(user: UserRetailer)
    suspend fun getUser(): UserRetailer

    suspend fun saveOrderStats(orderStats: OrderStats)
    suspend fun getOrderStats(): OrderStats

    suspend fun setUpdatedOrderStatus(updated: Boolean)
    suspend fun updatedOrderStatus(): Boolean

    suspend fun saveVendorCategories(categories: List<VendorCategory>)
    suspend fun getVendorCategories(): List<VendorCategory>

    suspend fun saveWorkHours(workHours: WorkHoursResponse)
    suspend fun getWorkHours(): WorkHoursResponse?

    suspend fun clearAll()

    suspend fun getUserId(): Int {
        return getUser().id
    }
}