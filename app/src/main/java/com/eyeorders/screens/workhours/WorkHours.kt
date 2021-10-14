package com.eyeorders.screens.workhours

import androidx.annotation.StringRes

data class WorkHours(
    val id: Int,
    @StringRes val day: Int,
    val hours: String,
)