package com.eyeorders.screens.workhours

data class WorkHoursViewState(
    val workHours: List<WorkHours> = emptyList(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
)