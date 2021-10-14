package com.eyeorders.util.date

import com.yariksoffice.lingver.Lingver
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class DateFormatter @Inject constructor() {
    private val dateTimeFormat =
        SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Lingver.getInstance().getLocale())
    private val dateFormat =
        SimpleDateFormat("d MMM, yyyy", Lingver.getInstance().getLocale())

    private val serverDateFormat =
        SimpleDateFormat("dd-MM-yyyy", Lingver.getInstance().getLocale())
    private val printerDate =
        SimpleDateFormat("dd.MM.yyyy HH:mm", Lingver.getInstance().getLocale())

    private val timeFormat = SimpleDateFormat("HH:mm a", Lingver.getInstance().getLocale())

    fun formatDateTime(timeStamp: Long): String {
        return dateTimeFormat.format(timeStamp * TIME_STAMP_MULTIPLIER)
    }

    fun formatDate(timeStamp: Long?): String {
        return timeStamp?.let { dateFormat.format(timeStamp * TIME_STAMP_MULTIPLIER) } ?: "-"
    }

    fun formatServerDate(date: Date): String {
        return serverDateFormat.format(date)
    }

    fun formatToTime(timeStamp: Long?): String {
        return timeStamp?.let { timeFormat.format(it * TIME_STAMP_MULTIPLIER) } ?: "-"
    }

    fun formatToPrinterDate(timeStamp: Long?): String {
        return timeStamp?.let { printerDate.format(it * TIME_STAMP_MULTIPLIER) } ?: "-"
    }

    companion object {
        private const val TIME_STAMP_MULTIPLIER = 1000
    }
}