package com.eyeorders.data.remote.api.deserialiser

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class IntTypeAdapter : TypeAdapter<Int>() {
    override fun read(reader: JsonReader): Int {
        val json = reader.peek()

        if (json == JsonToken.NULL) {
            return 0
        }

        if (json == JsonToken.STRING) {
            val jsonValue = reader.nextString()
            return try {
                jsonValue.toInt()
            } catch (e: NumberFormatException) {
                0
            }
        }

        return reader.nextInt()
    }

    override fun write(out: JsonWriter, value: Int?) {
        if (value == null) {
            out.value(0.0)
            return
        }
        out.value(value)
    }
}