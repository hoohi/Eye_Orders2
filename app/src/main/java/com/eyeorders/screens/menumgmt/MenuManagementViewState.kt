package com.eyeorders.screens.menumgmt

data class MenuManagementViewState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val listing: List<MenuCategory> = emptyList(),
)