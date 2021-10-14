package com.eyeorders.data.usecase.base

import com.eyeorders.data.exception.ServerException
import com.eyeorders.data.remote.api.response.ApiResponse
import com.eyeorders.data.remote.api.response.OrdersApiResponse

suspend fun <T> processResponse(apiCall: suspend () -> ApiResponse<T>): List<T> {
    val result = apiCall.invoke()
    if (result.status) {
        return result.data
    } else {
        throw ServerException(result.message, result)
    }
}

suspend fun <T> processOrdersResponse(apiCall: suspend () -> OrdersApiResponse<T>): OrdersApiResponse<T> {
    val result = apiCall.invoke()
    if (result.status) {
        return result
    } else {
        throw ServerException(result.message, result)
    }
}