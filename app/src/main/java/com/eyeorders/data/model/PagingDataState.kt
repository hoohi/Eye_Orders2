package com.eyeorders.data.model

sealed class PagingDataState<out T> {
    object Loading : PagingDataState<Nothing>()
    object EndOfPage : PagingDataState<Nothing>()
    data class Success<out T>(val data: T) : PagingDataState<T>()
    data class Error(val message: String) : PagingDataState<Nothing>()
}
