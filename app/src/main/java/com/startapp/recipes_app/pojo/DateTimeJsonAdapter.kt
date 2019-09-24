package com.startapp.recipes_app.pojo

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.joda.time.DateTime
import javax.inject.Inject


/**
 * convert joda-datetime from/to string
 * */

class DateTimeJsonAdapter @Inject constructor (): TypeAdapter<DateTime?>() {
    override fun write(out: JsonWriter?, value: DateTime?) {
        value?.let {
            out?.value(it.toString())
        }
    }

    override fun read(`in`: JsonReader?): DateTime? {
        try {
            return `in`?.let{DateTime(`in`.nextString())}
        } catch (e: Exception) {
        }
        return null
    }

}