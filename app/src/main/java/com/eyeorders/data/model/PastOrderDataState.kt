package com.eyeorders.data.model

sealed class PastOrderDataState<out T> {
    object Loading : PastOrderDataState<Nothing>()
    object CancelSelection : PastOrderDataState<Nothing>()
    object ChangeSelection : PastOrderDataState<Nothing>()
    data class Success<out T>(val data: T) : PastOrderDataState<T>()
    data class Error(val message: String) : PastOrderDataState<Nothing>()
}
