package com.eyeorders.screens.login

data class LoginViewState(
    val loading: Boolean = false,
    val loggedIn:Boolean = false,
    val passwordMessage: String? = null,
    val emailMessage: String? = null,
    val errorMessage: String? = null
)