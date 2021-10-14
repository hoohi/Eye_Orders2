package com.eyeorders.data.exception

class ServerException(message: String, val data: Any? = null) : Exception(message)