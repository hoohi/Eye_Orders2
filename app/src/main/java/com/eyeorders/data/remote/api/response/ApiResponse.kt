package com.eyeorders.data.remote.api.response

import com.eyeorders.data.remote.api.deserialiser.AlwaysListTypeAdapterFactory
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName


data class ApiResponse<T>(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data")
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    val data: List<T>,
)