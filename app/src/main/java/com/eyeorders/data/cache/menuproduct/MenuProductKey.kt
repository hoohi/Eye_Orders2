package com.eyeorders.data.cache.menuproduct

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MenuProductKey(
    @PrimaryKey val id: Int,
    val prevKey: Int?,
    val nextPage: Int?
)