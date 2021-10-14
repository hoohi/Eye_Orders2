package com.eyeorders.data.model.user

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("apiToken") val apiToken: String,
    @SerializedName("channel_id") val channelId: String,
)