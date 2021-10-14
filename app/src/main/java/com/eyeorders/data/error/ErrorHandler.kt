package com.eyeorders.data.error

interface ErrorHandler {
    fun getErrorMessage(e: Throwable?): String
}
