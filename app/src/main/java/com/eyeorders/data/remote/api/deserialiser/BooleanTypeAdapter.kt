package com.eyeorders.data.remote.api.deserialiser

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class BooleanTypeAdapter : TypeAdapter<Boolean?>() {
    override fun read(reader: JsonReader): Boolean {
        val json = reader.peek()

        if (json == JsonToken.NULL) {
            return false
        }

        if (json == JsonToken.STRING) {
            val jsonValue = reader.nextString()
            return when {
                jsonValue.equals("true", ignoreCase = true) -> {
                    true
                }
                jsonValue.equals("false", ignoreCase = true) -> {
                    false
                }
                jsonValue.isEmpty() -> {
                    false
                }
                else -> {
                    false
                }
            }
        }

        if (json == JsonToken.NUMBER) {
            return reader.nextInt() == 1
        }

        return reader.nextBoolean()
    }

    override fun write(out: JsonWriter, value: Boolean?) {
        if (value == null) {
            out.value(false)
            return
        }
        out.value(value)
    }
}