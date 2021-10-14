package com.eyeorders.data.remote.api.deserialiser

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class LongTypeAdapter : TypeAdapter<Long>() {
    override fun read(reader: JsonReader): Long {
        val json = reader.peek()

        if (json == JsonToken.NULL) {
            return 0L
        }

        if (json == JsonToken.STRING) {
            val jsonValue = reader.nextString()
            return try {
                jsonValue.toLong()
            } catch (e: NumberFormatException) {
                0L
            }
        }

        return reader.nextLong()
    }

    override fun write(out: JsonWriter, value: Long?) {
        if (value == null) {
            out.value(0.0)
            return
        }
        out.value(value)
    }
}