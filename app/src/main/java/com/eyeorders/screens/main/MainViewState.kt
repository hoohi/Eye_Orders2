package com.eyeorders.screens.main

data class MainViewState(
    val storeOpen: Boolean,
    val loading: Boolean = false,
    val errorMessage: String? = null,
)