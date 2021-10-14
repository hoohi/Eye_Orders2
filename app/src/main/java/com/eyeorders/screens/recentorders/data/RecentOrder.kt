package com.eyeorders.screens.recentorders.data

data class RecentOrder(
    val id: Int,
    val orderNumber:String,
    val dateTime:String,
    val status:String,
    val username:String,
    val userImageUrl:String
)