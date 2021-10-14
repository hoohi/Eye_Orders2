package com.eyeorders.data.remote.api.response

import com.google.gson.annotations.SerializedName

data class WorkHoursResponse(
    @SerializedName("sat_start")
    val satStart: String = "",
    @SerializedName("sat_end")
    val satEnd: String = "",
    @SerializedName("fri_end")
    val friEnd: String = "",
    @SerializedName("mon_end")
    val monEnd: String = "",
    @SerializedName("wed_start")
    val wedStart: String = "",
    @SerializedName("sun_end")
    val sunEnd: String = "",
    @SerializedName("store_timezone")
    val storeTimezone: String = "",
    @SerializedName("tue_start")
    val tueStart: String = "",
    @SerializedName("sun_start")
    val sunStart: String = "",
    @SerializedName("mon_start")
    val monStart: String = "",
    @SerializedName("tue_end")
    val tueEnd: String = "",
    @SerializedName("thu_end")
    val thuEnd: String = "",
    @SerializedName("thu_start")
    val thuStart: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("wed_end")
    val wedEnd: String = "",
    @SerializedName("fri_start")
    val friStart: String = ""
)