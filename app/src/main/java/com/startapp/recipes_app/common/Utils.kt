package com.startapp.recipes_app.common

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.InputStream
import java.io.InputStreamReader


/**
 * for the sake of this assignment - this class helps deserialising the JSONs to POJOs
 */
object Utils {

    private val gson: Gson = Gson()


    @JvmStatic
    fun <T> readObject(resourcePath: String, klass: Class<T>): T? {

        val streamReader =
            InputStreamReader(ClassLoader.getSystemResourceAsStream(resourcePath), "UTF-8")
        return readObjectFromStream(streamReader, klass)

    }

    @JvmStatic
    fun <T> readObject(stream: InputStream, klass: Class<T>): T? {
        val streamReader = InputStreamReader(stream)
        return readObjectFromStream(streamReader, klass)

    }


    private fun <T> readObjectFromStream(streamReader: InputStreamReader, klass: Class<T>): T? {
        val jsonReader = JsonReader(streamReader)
        try {
            return gson.fromJson(jsonReader, klass)
        } finally {
            jsonReader.close()
        }
    }
}