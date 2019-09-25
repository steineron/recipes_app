package com.startapp.recipes_app.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.startapp.recipes_app.patterns.Observable
import com.startapp.recipes_app.patterns.Observer
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import java.io.InputStream
import java.io.InputStreamReader

// capturer for observations in tests
class Observations<T> : Observer<T> {


    val observations: MutableList<T?> = mutableListOf()

    override fun observed(value: T?, oldValue: T?, observable: Observable<T>) {
        observations.add(value)
    }
}

// static test util methods:
object TestUtils {

    val testScheduler: TestScheduler = TestScheduler()

    private val trampolineMain: Scheduler = Schedulers.trampoline()

    @JvmStatic
    fun convertAllSchedulersToTest() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { testScheduler }
        RxAndroidPlugins.setMainThreadSchedulerHandler { testScheduler }
        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        RxJavaPlugins.setNewThreadSchedulerHandler { testScheduler }
        RxJavaPlugins.setSingleSchedulerHandler { testScheduler }
    }

    /* this method is used to setup the main thread to a different Scheduler.
    * this way, we can try to verify some of the tasks are happening on the
    * different thread, by verifying the instance id from debugging.
    *
    * NOTE: This method is only used for Domain Account Model Test for now.
    * Normally, most of the unit tests should ignore this setup.
    */
    @JvmStatic
    fun convertMainSchedulersToTrampoline() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { trampolineMain }
        RxAndroidPlugins.setMainThreadSchedulerHandler { trampolineMain }
        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        RxJavaPlugins.setNewThreadSchedulerHandler { testScheduler }
        RxJavaPlugins.setSingleSchedulerHandler { testScheduler }
    }


    @JvmStatic
    fun resetSchedulers() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }



    @JvmStatic
    fun areDrawablesIdentical(drawableA: Drawable, drawableB: Drawable): Boolean {
        val stateA = drawableA.constantState
        val stateB = drawableB.constantState
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true - so compare bitmaps 1-to-1:
        return stateA != null && stateB != null && stateA == stateB || getBitmap(drawableA).sameAs(getBitmap(drawableB))
    }

    private fun getBitmap(drawable: Drawable): Bitmap {
        val result: Bitmap
        if (drawable is BitmapDrawable) {
            result = drawable.bitmap
        } else {
            var width = drawable.intrinsicWidth
            var height = drawable.intrinsicHeight
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1
            }
            if (height <= 0) {
                height = 1
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return result
    }


}