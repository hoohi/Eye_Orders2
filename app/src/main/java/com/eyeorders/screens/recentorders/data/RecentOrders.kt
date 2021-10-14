package com.eyeorders.screens.recentorders.data

data class RecentOrders(
    val completedOrders: String,
    val cancelledOrders: String,
    val count: Int,
    val timeStart: String,
    val timeEnd: String,
    val orders: List<RecentOrder>
) {
    companion object {
        val EMPTY = RecentOrders(
            "0",
            "0",
            0,
            "",
            "",
            emptyList()
        )
    }
}