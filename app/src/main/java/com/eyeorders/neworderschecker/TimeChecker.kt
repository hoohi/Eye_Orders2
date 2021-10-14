package com.eyeorders.neworderschecker

import com.eyeorders.data.prefs.PrefsDataManager
import javax.inject.Inject

class TimeChecker @Inject constructor(
    private val workingHoursChecker: WorkingHoursChecker,
    private val prefsDataManager: PrefsDataManager,
) {

    suspend fun isWithinTime(): Boolean {
        val storeOpen = prefsDataManager.getOrderStats().isStopOrder.not()
        return workingHoursChecker.isWithinWorkHoursToday() && storeOpen
    }

}