package com.eyeorders.screens.recentorders.past

import com.eyeorders.data.model.EndDate
import com.eyeorders.data.model.StartDate

sealed class PastOrderIntent {
    data class LoadOrders(val startDate: StartDate, val endDate: EndDate) : PastOrderIntent()
    object CancelSelection : PastOrderIntent()
    object ChangeSelection : PastOrderIntent()
}