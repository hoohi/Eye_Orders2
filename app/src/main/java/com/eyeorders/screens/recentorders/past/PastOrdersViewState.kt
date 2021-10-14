package com.eyeorders.screens.recentorders.past

import com.eyeorders.screens.recentorders.data.RecentOrders
import com.eyeorders.screens.recentorders.data.RecentOrders.Companion.EMPTY

data class PastOrdersViewState(
    val searchMode: Boolean = true,
    val loading: Boolean = false,
    val empty: Boolean = false,
    val firstLoad: Boolean = false,
    val errorMessage: String? = null,
    val recentOrders: RecentOrders = EMPTY
)