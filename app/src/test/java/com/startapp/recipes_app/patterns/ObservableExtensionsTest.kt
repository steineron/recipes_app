package com.startapp.recipes_app.patterns

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ObservableExtensionsTest {

    private lateinit var subject: Subject<String>
    private lateinit var observer: Observations

    private val texts = listOf("1", "2", "3", "4", "5")


    // simply collect all observations for later analysis
    private class Observations : Observer<String> {
        val observations = mutableListOf<String?>()
        override fun observed(value: String?, oldValue: String?, observable: Observable<String>) {
            observations.add(value)
        }
    }

    @Before
    fun setUp() {
        subject = Subject()
        observer = Observations()
    }

    @Test
    fun that_observe_an_observable_works() {
        observer.observe(subject)
        subject.emit("text")
        with(observer.observations) {
            assertTrue(contains("text"))
            assertTrue(size == 1)
        }
    }

    @Test
    fun that_disregard_an_observable_works() {
        observer.observe(subject)
        subject.emit("text")
        with(observer.observations) {
            assertTrue(contains("text"))
            assertTrue(size == 1)
        }

        observer.disregard(subject)
        subject.emit("more text")
        with(observer.observations) {
            assertFalse(contains("more text"))
            assertTrue(size == 1)
        }

    }

}