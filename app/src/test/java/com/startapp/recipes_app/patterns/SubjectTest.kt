package com.startapp.recipes_app.patterns

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SubjectTest {

    private lateinit var subject: Subject<String>
    private lateinit var nullableStringSubject: Subject<String?>
    private lateinit var sampleSubject: Subject<Sample?>

    private lateinit var observer: Observations
    private lateinit var nullableObserver: NullableObservations
    private lateinit var sampleObserver: SampleObservations

    private val texts = listOf("1", "2", "3", "4", "5")
    private val nullTexts = listOf(null, null, null)
    private val mixedTexts = listOf("1", null)
    private val stringList = mutableListOf<String?>()
    private val sampleList = mutableListOf<Sample?>()


    private class Sample(val i: Int)

    // simply collect all observations for later analysis
    private class Observations : Observer<String> {
        val observations = mutableListOf<String?>()
        override fun observed(value: String?, oldValue: String?, observable: Observable<String>) {
            observations.add(value)
        }
    }

    private class NullableObservations : Observer<String?> {
        val observations = mutableListOf<String?>()
        override fun observed(value: String?, oldValue: String?, observable: Observable<String?>) {
            observations.add(value)
        }
    }

    private class SampleObservations : Observer<Sample?> {
        val observations = mutableListOf<Sample?>()
        override fun observed(value: Sample?, oldValue: Sample?, observable: Observable<Sample?>) {
            observations.add(value)
        }
    }


    @Before
    fun setUp() {
        subject = Subject()
        nullableStringSubject = Subject()
        sampleSubject = Subject()

        observer = Observations()
        nullableObserver = NullableObservations()
        sampleObserver = SampleObservations()
    }

    @Test
    fun that_registering_after_emission_doesnt_result_in_emission() {
        texts.forEach {
            subject.emit(it)
        }

        with(observer.observations) {
            assertTrue(isEmpty())
        }

        subject.add(observer)

        // nothing changed
        with(observer.observations) {
            assertTrue(isEmpty())
        }
    }

    @Test
    fun that_observing_results_in_observations() {

        subject.add(observer)

        with(observer.observations) {
            assertTrue(isEmpty())
        }

        texts.forEach {
            subject.emit(it)
        }

        with(observer.observations) {
            texts.forEach {
                assertTrue(contains(it))
            }
            assertTrue(size == 5)
        }
    }

    @Test
    fun that_observing_results_only_in_new_observations() {

        // emit first
        subject.emit(texts[0])
        subject.emit(texts[1])

        with(observer.observations) {
            assertTrue(isEmpty())
        }

        // observe
        subject.add(observer)
        // still empty
        with(observer.observations) {
            assertTrue(isEmpty())
        }


        subject.emit(texts[2])

        // emmit again
        subject.emit(texts[3])
        subject.emit(texts[4])

        with(observer.observations) {
            assertFalse(contains(texts[0]))
            assertFalse(contains(texts[1]))
            assertTrue(contains(texts[2]))
            assertTrue(contains(texts[3]))
            assertTrue(contains(texts[4]))
            assertTrue(size == 3)
        }
    }

    @Test
    fun that_observing_results_in_observations_only_during_registration() {

        // emit some
        subject.emit(texts[0])
        subject.emit(texts[1])

        with(observer.observations) {
            assertTrue(isEmpty())
        }

        // observe
        subject.add(observer)

        // emit
        subject.emit(texts[2])

        // stop observing
        subject.remove(observer)
        // emit more
        subject.emit(texts[3])
        subject.emit(texts[4])

        with(observer.observations) {
            assertFalse(contains(texts[0]))
            assertFalse(contains(texts[1]))
            assertTrue(contains(texts[2]))
            assertFalse(contains(texts[3]))
            assertFalse(contains(texts[4]))
            assertTrue(size == 1)
        }
    }

    @Test
    fun that_multiple_registration_of_observer_result_in_single_observation_per_emission() {

        subject.add(observer)
        subject.add(observer)
        subject.add(observer)
        subject.add(observer)

        with(observer.observations) {
            assertTrue(isEmpty())
        }

        subject.emit(texts[0])

        with(observer.observations) {
            assertTrue(contains(texts[0]))
            assertTrue(size == 1)
        }
    }

    @Test
    fun that_emitting_the_same_value_repeatedly_results_in_multiple_emissions() {
        subject.add(observer)
        with(observer.observations) {
            assertTrue(isEmpty())
        }
        subject.emit("text")
        subject.emit("text")
        subject.emit("text")
        subject.emit("text")
        with(observer.observations) {
            assertTrue(contains("text"))
            assertTrue(size == 4)
        }

    }

    @Test
    fun that_subject_emits_to_all_observers() {
        val observers: Array<Observations> = Array(10) { Observations() }
        observers.forEach {
            subject.add(it)
            with(it.observations) {
                assertTrue(isEmpty())
            }
        }

        subject.emit("text")

        observers.forEach { its ->

            with(its.observations) {
                assertTrue(contains("text"))
                assertTrue(size == 1)
            }

        }
    }

    @Test
    fun that_subject_emits_to_registered_observers() {
        val observers: Array<Observations> = Array(10) { Observations() }
        observers.forEach { observer ->
            subject.add(observer)
            with(observer.observations) {
                assertTrue(isEmpty())
            }
        }

        subject.emit("text")

        observers.forEach { its ->

            with(its.observations) {
                assertTrue(contains("text"))
                assertTrue(size == 1)
            }
        }

        // remove the first 5 observers
        observers.filterIndexed { index, _ -> index < 5 }.forEach { subject.remove(it) }

        subject.emit("more text")

        observers.forEachIndexed { index, its ->

            with(its.observations) {
                // all contains "text" but the first 5 don't contain "more text"
                assertTrue(contains("text"))
                assertTrue(if (index < 5) size == 1 else (size == 2))
                assertTrue(if (index < 5) !contains("more text") else (contains("more text")))
            }
        }

    }

    @Test
    fun that_removing_all_observers_results_in_zero_emissions() {
        val observers: Array<Observations> = Array(10) { Observations() }
        observers.forEach { observer ->
            subject.add(observer)

            with(observer.observations) {
                assertTrue(isEmpty())
            }
        }

        subject.emit("text")

        observers.forEach { its ->

            with(its.observations) {
                assertTrue(contains("text"))
                assertTrue(size == 1)
            }
        }

        // remove all of them and emit again
        subject.removeAllObservers()

        subject.emit("another text")

        // observers haven't received an update:
        observers.forEach { its ->

            with(its.observations) {
                assertTrue(contains("text"))
                assertTrue(size == 1)
            }
        }

        // add them again:
        observers.forEach { observer ->
            subject.add(observer)
        }

        subject.emit("another text")

        // observers received an update:
        observers.forEach { its ->

            with(its.observations) {
                assertTrue(contains("text"))
                assertTrue(contains("another text"))
                assertTrue(size == 2)
            }
        }
    }

    @Test
    fun that_observing_multiple_subjects_results_in_multiple_emissions() {

        val anotherSubject: Subject<String> = Subject()

        observer.observe(subject)
        observer.observe(anotherSubject)

        with(observer.observations) {
            assertTrue(isEmpty())
        }

        subject.emit("text")
        anotherSubject.emit("another text")

        with(observer.observations) {
            assertTrue(contains("text"))
            assertTrue(contains("another text"))
            assertTrue(size == 2)
        }

    }

    @Test
    fun that_removing_unregistered_observer_works() {

        // had no observations
        with(observer.observations) {
            assertTrue(isEmpty())
        }

        observer.disregard(subject) // doesn't fail because of an unknown observer

        // still no observations
        with(observer.observations) {
            assertTrue(isEmpty())
        }

        subject.emit("value")

        // no observations
        with(observer.observations) {
            assertTrue(isEmpty())
        }

    }

    @Test
    fun that_subject_emits_correct_old_value() {

        class Recorder : Observer<String> {
            var newVal: String? = null
            var oldVal: String? = null
            override fun observed(value: String?, oldValue: String?, observable: Observable<String>) {
                newVal = value
                oldVal = oldValue
            }

        }

        val recorder = Recorder()

        recorder.observe(subject)

        subject.emit("first")

        with(recorder) {
            assertNull(oldVal)
            assertTrue("first" == newVal)
        }

        subject.emit("second")

        with(recorder) {
            assertTrue("first" == oldVal)
            assertTrue("second" == newVal)
        }
    }

    @Test
    fun `that null values get emitted`() {
        nullableStringSubject.add(nullableObserver)

        with(nullableObserver.observations) {
            assertTrue(isEmpty())
        }

        nullTexts.forEach {
            nullableStringSubject.emit(it)
        }

        with(nullableObserver.observations) {
            nullTexts.forEachIndexed { index, nothing ->
                assertTrue(get(index) == nothing)
            }
            assertTrue(size == 3)
        }
    }

    @Test
    fun `that null and non null values get emitted`() {
        nullableStringSubject.add(nullableObserver)

        with(nullableObserver.observations) {
            assertTrue(isEmpty())
        }

        mixedTexts.forEach {
            nullableStringSubject.emit(it)
        }

        with(nullableObserver.observations) {
            mixedTexts.forEachIndexed { index, s ->
                assertTrue(get(index) == s)
            }
            assertTrue(size == 2)
        }
    }

    @Test
    fun `that nullable objects get emitted`() {
        stringList.add(null)
        stringList.add("One")
        stringList.add("Two")
        stringList.add(null)
        nullableStringSubject.add(nullableObserver)
        with(nullableObserver.observations) {
            assertTrue(isEmpty())
        }

        stringList.forEach {
            nullableStringSubject.emit(it)
        }

        with(nullableObserver.observations) {
            stringList.forEachIndexed { index, s ->
                assertTrue(get(index) == s)
            }
            assertTrue(size == 4)
        }
    }


    @Test
    fun `that null objects of Sample get emitted`() {
        sampleList.add(Sample(1))
        sampleList.add(null)
        sampleList.add(Sample(2))
        sampleSubject.add(sampleObserver)
        with(sampleObserver.observations) {
            assertTrue(isEmpty())
        }

        sampleList.forEach {
            sampleSubject.emit(it)
        }

        assertTrue(sampleObserver.observations.size == 3)
        sampleObserver.observations.forEachIndexed { index, sample ->
            if (sample == null) {
                assertTrue(sampleList[index] == null)
            } else {
                assertTrue(sampleList[index]?.i == sample.i)
            }
        }
    }
}