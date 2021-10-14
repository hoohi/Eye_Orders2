package com.eyeorders.screens.recentorders.today

import com.eyeorders.screens.recentorders.data.RecentOrders
import com.eyeorders.screens.recentorders.data.RecentOrders.Companion.EMPTY

data class TodayOrdersViewState(
    val loading: Boolean = false,
    val empty: Boolean = false,
    val errorMessage: String? = null,
    val recentOrders: RecentOrders = EMPTY
)