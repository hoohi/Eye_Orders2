package com.eyeorders.util.coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

suspend fun loop(timeDelayInMilliseconds: Long, block: suspend () -> Unit) {
    while (true) {
        block()
        withContext(Dispatchers.IO) { delay(timeDelayInMilliseconds) }
    }
}