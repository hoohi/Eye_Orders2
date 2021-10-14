package com.eyeorders.util.date

import com.yariksoffice.lingver.Lingver
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DateTimeParser @Inject constructor() {

    private val timeFormat = SimpleDateFormat("HH:mm", Lingver.getInstance().getLocale())

    fun convertToDate(year: Int, month: Int, day: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        return calendar.time
    }

    fun convertToYearMonthDay(date: Date? = null): Triple<Int, Int, Int> {
        val calendar = Calendar.getInstance()
        date?.let {
            calendar.time = it
        }
        return Triple(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun parseTime(time: String): Date? {
        return if (time.isNotEmpty()) {
            timeFormat.parse(time)
        } else {
            null
        }
    }
}