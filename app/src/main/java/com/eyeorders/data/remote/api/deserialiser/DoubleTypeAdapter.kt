package com.eyeorders.data.remote.api.deserialiser

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class DoubleTypeAdapter : TypeAdapter<Double>() {
    override fun read(reader: JsonReader): Double {
        val json = reader.peek()

        if (json == JsonToken.NULL) {
            return 0.0
        }

        if (json == JsonToken.STRING) {
            val jsonValue = reader.nextString()
            return try {
                jsonValue.toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }
        }

        return reader.nextDouble()
    }

    override fun write(out: JsonWriter, value: Double?) {
        if (value == null) {
            out.value(0.0)
            return
        }
        out.value(value)
    }
}