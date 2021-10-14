package com.eyeorders.screens.activeorders.neworders

data class NewOrderViewState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val data: List<NewOrder> = emptyList()
)