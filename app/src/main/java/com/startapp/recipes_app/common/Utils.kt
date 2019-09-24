package com.startapp.recipes_app.common

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.InputStream
import java.io.InputStreamReader


/**
 * for the sake of this assignment - this class helps deserialising the JSONs to POJOs
 */
object Utils {


    @JvmStatic
    fun <T> readObject(resourcePath: String, klass: Class<T>, gson: Gson): T? {

        val streamReader =
            InputStreamReader(ClassLoader.getSystemResourceAsStream(resourcePath), "UTF-8")
        return readObjectFromStream(streamReader, klass, gson)

    }

    @JvmStatic
    fun <T> readObject(stream: InputStream, klass: Class<T>, gson: Gson): T? {
        val streamReader = InputStreamReader(stream)
        return readObjectFromStream(streamReader, klass, gson)

    }


    @JvmStatic
    fun <T> readObjectFromStream(
        streamReader: InputStreamReader,
        klass: Class<T>,
        gson: Gson
    ): T? {
        val jsonReader = JsonReader(streamReader)
        try {
            return gson.fromJson(jsonReader, klass)
        } finally {
            jsonReader.close()
        }
    }
}