package com.eyeorders.screens.activeorders

import androidx.paging.PagingData

data class ActiveOrder<T : Any>(
    val orderCount: String = "0",
    val orders: PagingData<T> = PagingData.empty()
)