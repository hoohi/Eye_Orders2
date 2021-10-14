package com.eyeorders.neworderschecker

import com.eyeorders.data.prefs.PrefsDataManager
import com.eyeorders.data.usecase.workhours.SyncWorkHoursUseCase
import com.eyeorders.util.date.DateTimeParser
import org.threeten.bp.Instant
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class WorkingHoursChecker @Inject constructor(
    private val dateTimeParser: DateTimeParser,
    private val prefsDataManager: PrefsDataManager,
    private val syncWorkHoursUseCase: SyncWorkHoursUseCase
) {

    suspend fun isWithinWorkHoursToday(): Boolean {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        syncWorkHoursUseCase.execute()

        val workHours = prefsDataManager.getWorkHours()
            ?: throw WorkHoursNotSyncedException("Work hours should have been synced")

        Timber.d("Work hours: $workHours")

        return when (dayOfWeek) {
            Calendar.SUNDAY -> {
                val startTime = parseTime(workHours.sunStart)
                val endTime = parseTime(workHours.sunEnd)
                val now = Calendar.getInstance().time
                val withinTime = isWithinTime(startTime, endTime, now)
                Timber.d("Within time for SUNDAY? $withinTime")
                withinTime
            }

            Calendar.MONDAY -> {
                val startTime = parseTime(workHours.monStart)
                val endTime = parseTime(workHours.monEnd)
                val now = Calendar.getInstance().time
                val withinTime = isWithinTime(startTime, endTime, now)
                Timber.d("Within time for MONDAY? $withinTime")
                withinTime

            }

            Calendar.TUESDAY -> {
                val startTime = parseTime(workHours.tueStart)
                val endTime = parseTime(workHours.tueEnd)
                val now = Calendar.getInstance().time
                val withinTime = isWithinTime(startTime, endTime, now)
                Timber.d("Within time for TUESDAY? $withinTime")
                withinTime
            }

            Calendar.WEDNESDAY -> {
                val startTime = parseTime(workHours.wedStart)
                val endTime = parseTime(workHours.wedEnd)
                val now = Calendar.getInstance().time
                val withinTime = isWithinTime(startTime, endTime, now)
                Timber.d("Within time for WEDNESDAY? $withinTime")
                withinTime
            }

            Calendar.THURSDAY -> {
                val startTime = parseTime(workHours.thuStart)
                val endTime = parseTime(workHours.thuEnd)
                val now = Calendar.getInstance().time
                val withinTime = isWithinTime(startTime, endTime, now)
                Timber.d("Within time for THURSDAY? $withinTime")
                withinTime
            }

            Calendar.FRIDAY -> {
                val startTime = parseTime(workHours.friStart)
                val endTime = parseTime(workHours.friEnd)
                val now = Calendar.getInstance().time
                val withinTime = isWithinTime(startTime, endTime, now)
                Timber.d("FRIDAY: $startTime --- $endTime --- $now")
                Timber.d("Within time for FRIDAY? $withinTime")
                withinTime
            }

            Calendar.SATURDAY -> {
                val startTime = parseTime(workHours.satStart)
                val endTime = parseTime(workHours.satEnd)
                val now = Calendar.getInstance().time
                val withinTime = isWithinTime(startTime, endTime, now)
                Timber.d("Within time for SATURDAY? $withinTime")
                withinTime
            }
            else -> {
                throw IllegalStateException("Unexpected day of week: $dayOfWeek")
            }
        }
    }

    private fun parseTime(time: String): Date {
        val date = dateTimeParser.parseTime(time) ?: Date(Instant.EPOCH.toEpochMilli())
        val calendar = Calendar.getInstance()
        calendar.time = date
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
        today.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
        return today.time
    }

    private fun isWithinTime(intervalStart: Date, intervalEnd: Date, compareTime: Date): Boolean {
        return compareTime.after(intervalStart) and compareTime.before(intervalEnd)
    }

    class WorkHoursNotSyncedException(message: String?) : IllegalStateException(message)
}