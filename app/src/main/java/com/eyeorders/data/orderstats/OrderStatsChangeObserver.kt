package com.eyeorders.data.orderstats

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.eyeorders.data.prefs.PrefsDataManager
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderStatsChangeObserver @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val prefsDataManager: PrefsDataManager,
) {

    fun observeOrderStatsChange(): Flow<OrderStats> = callbackFlow {
        emitStoreStatus()
        val listener = OnSharedPreferenceChangeListener { _, key ->
            if (key == OrderStats.KEY) {
                launch {
                    emitStoreStatus()
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private suspend fun ProducerScope<OrderStats>.emitStoreStatus() {
        val countChange = prefsDataManager.getOrderStats()
        Timber.d("countChange: $countChange")
        offer(countChange)
    }
}