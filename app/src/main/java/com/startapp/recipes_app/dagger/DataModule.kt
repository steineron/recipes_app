package com.startapp.recipes_app.dagger

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.startapp.recipes_app.pojo.DateTimeJsonAdapter
import dagger.Module
import dagger.Provides
import org.joda.time.DateTime


/**
 * provide the data-relates instances to the dependencies DAG
 */

@Module
object DataModule {

    @Provides
    @JvmStatic
    internal fun provideGson(dateAdapter: TypeAdapter<DateTime?>): Gson {
        return GsonBuilder()
            .registerTypeAdapter(DateTime::class.java,dateAdapter )
            .create()
    }

    @Provides
    @JvmStatic
    fun provideDateTimeAdapter(adapter: DateTimeJsonAdapter):TypeAdapter<DateTime?> = adapter
}