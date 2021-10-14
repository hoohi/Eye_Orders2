package com.eyeorders.data.remote.api.response.login

import com.eyeorders.data.model.user.User
import com.eyeorders.data.model.user.UserRetailer
import com.google.gson.annotations.SerializedName


data class LoginResponse(
    @SerializedName("user") val user: User,
    @SerializedName("userRetailer") val userRetailer: UserRetailer,
)