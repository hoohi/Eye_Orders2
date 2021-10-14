package com.eyeorders.screens.base

data class BaseUiState(
    val loading: Boolean = false,
    val empty: Boolean = false,
    val errorMessage: String? = null,
)