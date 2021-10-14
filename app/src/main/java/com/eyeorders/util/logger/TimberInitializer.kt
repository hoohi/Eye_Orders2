package com.eyeorders.util.logger

import com.tasleem.orders.BuildConfig
import timber.log.Timber
import javax.inject.Inject

class TimberInitializer @Inject constructor(
    private val crashlyticsTree: CrashlyticsTree
) {
    fun init() {
        Timber.plant(
            if (BuildConfig.DEBUG) {
                Timber.DebugTree()
            } else {
                crashlyticsTree
            }
        )
    }
}