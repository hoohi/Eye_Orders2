package com.eyeorders.data.mockdata.fakes

import java.util.UUID
import kotlin.random.Random

object PrimitiveDataFactory {

    fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    fun randomInt(from: Int = 1, to: Int = 1001): Int {
        return Random.nextInt(from, to)
    }

    fun randomLong(): Long {
        return randomInt().toLong()
    }

    fun randomBoolean(): Boolean {
        return Math.random() < 0.5
    }
}