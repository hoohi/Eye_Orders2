package com.eyeorders.data.usecase.orders.mapper

interface Mapper<in R, out U> {
    fun map(entity: R): U
}