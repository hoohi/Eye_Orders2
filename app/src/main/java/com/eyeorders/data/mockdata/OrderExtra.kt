package com.eyeorders.data.mockdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderExtra(
    val id: String,
    val item: Item,
    val quantity: Int,
    val amount: String,
) : Parcelable