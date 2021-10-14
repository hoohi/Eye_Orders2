package com.eyeorders.data.mockdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class Order(
    val id: String,
    val number: String,
    val status: String,
    val action: String,
    val dateTime: String,
    val amount: BigDecimal,
    val currency: String,
    val user: User,
    val notes:String,
    val items:List<OrderItem>
) : Parcelable