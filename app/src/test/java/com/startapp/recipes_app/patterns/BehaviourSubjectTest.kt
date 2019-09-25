package com.startapp.recipes_app.patterns

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BehaviourSubjectTest {

    private lateinit var subject: BehaviourSubject<String>
    private lateinit var nullableStringSubject: Subject<String?>
    private lateinit var sampleSubject: Subject<BehaviourSample?>

    private lateinit var observer: Observations
    private lateinit var nullableObserver: NullableObservations
    private lateinit var sampleObserver: BehaviourSampleObservations

    private val texts = listOf("1", "2", "3", "4", "5")
    private val nullTexts = listOf(null, null, null)
    private val mixedTexts = listOf("1", null)
    private val stringList = mutableListOf<String?>()
    private val sampleList = mutableListOf<BehaviourSample?>()

    private class BehaviourSample(val i: Int)

    // simply collect all observations for later analysis
    private class Observations : Observer<String> {
        val observations = mutableListOf<String?>()
        override fun observed(value: String?, oldValue: String?, observable: Observable<String>) {
            observations.add(value)
        }
    }

    private class NullableObservations : Observer<String?> {
        val previousObservations = mutableListOf<String?>()
        val observations = mutableListOf<String?>()
        override fun observed(value: String?, oldValue: String?, observable: Observable<String?>) {
            observations.add(value)
            previousObservations.add(oldValue)
        }
    }

    private class BehaviourSampleObservations : Observer<BehaviourSample?> {
        val previousObservations = mutableListOf<BehaviourSample?>()
        val observations = mutableListOf<BehaviourSample?>()
        override fun observed(value: BehaviourSample?, oldValue: BehaviourSample?, observable: Observable<BehaviourSample?>) {
            observations.add(value)
            previousObservations.add(oldValue)
        }
    }

    @Before
    fun setUp() {
        subject = BehaviourSubject()
        nullableStringSubject = BehaviourSubject()
        sampleSubject = BehaviourSubject()

        observer = Observations()
        nullableObserver = NullableObservations()
        sampleObserver = BehaviourSampleObservations()
    }

    @Test
    fun that_registering_after_emission_emits_the_last_item() {
        // emit all the texts
        texts.forEach {
            subject.emit(it)
        }

        with(observer.observations) {
            assertTrue(isEmpty())
        }

        subject.add(observer)

        // only the LAST text emitted upon registration
        with(observer.observations) {
            assertTrue(contains(texts.last()))
            assertTrue(size == 1)
        }
    }

    @Test
    fun that_observing_an_observer_with_no_emission_history_results_in_zero_emissions() {

        subject.add(observer)

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
        // emit all the texts
        texts.forEach {
            subject.emit(it)
        }
        // all texts emitted
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

        // emit a second
        subject.emit(texts[1])

        with(observer.observations) {
            assertTrue(isEmpty())
        }

        // observe - should emit the last
        subject.add(observer)

        // emmit again
        subject.emit(texts[2])
        subject.emit(texts[3])
        subject.emit(texts[4])

        with(observer.observations) {
            assertFalse(contains(texts[0]))
            assertTrue(contains(texts[1])) // the last value
            assertTrue(contains(texts[2]))
            assertTrue(contains(texts[3]))
            assertTrue(contains(texts[4]))
            assertTrue(size == 4)
        }
    }

    @Test
    fun that_observing_results_in_observations_of_the_last_emission_before_registration() {

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

            assertTrue(contains(texts[1])) // the value emitted before observing
            assertTrue(contains(texts[2])) // the value emitted after observing

            // values emitted after disregarding
            assertFalse(contains(texts[3]))
            assertFalse(contains(texts[4]))

            assertTrue(size == 2)
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
    }

    @Test
    fun that_subject_emits_only_to_registered_observers() {
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

        // observers received the latest update:
        observers.forEach { its ->

            with(its.observations) {
                assertTrue(contains("text"))
                assertTrue(contains("another text"))
                assertTrue(size == 2)
            }
        }

        subject.emit("more text")
        observers.forEach { its ->

            with(its.observations) {
                assertTrue(contains("text"))
                assertTrue(contains("another text"))
                assertTrue(contains("more text"))
                assertTrue(size == 3)
            }
        }
    }

    @Test
    fun that_observing_multiple_subjects_results_in_multiple_emissions() {

        val anotherSubject: Subject<String> = BehaviourSubject()

        subject.emit("text")
        anotherSubject.emit("another text")

        with(observer.observations) {
            assertTrue(isEmpty())
        }

        observer.observe(subject)
        observer.observe(anotherSubject)


        with(observer.observations) {
            assertTrue(contains("text"))
            assertTrue(contains("another text"))
            assertTrue(size == 2)
        }

        subject.emit("TEXT")
        anotherSubject.emit("ANOTHER TEXT")

        with(observer.observations) {
            assertTrue(contains("text"))
            assertTrue(contains("another text"))
            assertTrue(contains("TEXT"))
            assertTrue(contains("ANOTHER TEXT"))
            assertTrue(size == 4)
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
    fun that_subject_emits_null_old_value_for_newly_added_observers() {

        class Recorder : Observer<String> {
            var newVal: String? = null
            var oldVal: String? = null
            override fun observed(value: String?, oldValue: String?, observable: Observable<String>) {
                newVal = value
                oldVal = oldValue
            }

        }

        subject.emit("first")
        subject.emit("second")

        val recorder = Recorder()

        recorder.observe(subject)

        with(recorder) {
            assertNull(oldVal) // old val isn't "first" as this observer was added after the emission of second - to it has no history of observations
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

        assertTrue(nullTexts.size == 3)
        assertTrue(nullableObserver.observations.size == nullTexts.size)
        assertTrue(nullableObserver.previousObservations.size == nullTexts.size)
        with(nullableObserver.observations) {
            nullTexts.forEachIndexed { index, s ->
                assertTrue(get(index) == s)
            }
        }
        //removes the first value in the previous observation which is the last value = null
        nullableObserver.previousObservations.removeAt(0)
        nullableObserver.previousObservations.forEachIndexed { index, s ->
            assertTrue(nullTexts[index] == s)
        }
    }

    @Test
    fun `that null and non null strings get emitted`() {
        nullableStringSubject.add(nullableObserver)
        with(nullableObserver.observations) {
            assertTrue(isEmpty())
        }

        mixedTexts.forEach {
            nullableStringSubject.emit(it)
        }

        assertTrue(mixedTexts.size == 2)
        assertTrue(nullableObserver.observations.size == mixedTexts.size)
        assertTrue(nullableObserver.previousObservations.size == mixedTexts.size)
        with(nullableObserver.observations) {
            mixedTexts.forEachIndexed { index, s ->
                assertTrue(get(index) == s)
            }
        }

        //removes the first value in the previous observation which is the last value = null
        nullableObserver.previousObservations.removeAt(0)
        nullableObserver.previousObservations.forEachIndexed { index, s ->
            assertTrue(mixedTexts[index] == s)
        }
    }

    @Test
    fun `that null string in a list gets emitted`() {
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

        assertTrue(stringList.size == 4)
        assertTrue(nullableObserver.observations.size == stringList.size)
        assertTrue(nullableObserver.previousObservations.size == stringList.size)
        with(nullableObserver.observations) {
            stringList.forEachIndexed { index, s ->
                assertTrue(get(index) == s)
            }
        }
        //removes the first value in the previous observation which is the last value = null
        nullableObserver.previousObservations.removeAt(0)
        nullableObserver.previousObservations.forEachIndexed { index, s ->
            assertTrue(stringList[index] == s)
        }
    }


    @Test
    fun `that null objects of sample get emitted`() {
        sampleList.add(BehaviourSample(1))
        sampleList.add(null)
        sampleList.add(BehaviourSample(2))
        sampleSubject.add(sampleObserver)
        with(sampleObserver.observations) {
            assertTrue(isEmpty())
        }

        sampleList.forEach {
            sampleSubject.emit(it)
        }

        assertTrue(sampleList.size == 3)
        assertTrue(sampleObserver.observations.size == sampleList.size)
        assertTrue(sampleObserver.previousObservations.size == sampleList.size)
        sampleObserver.observations.forEachIndexed { index, sample ->
            if (sample == null) {
                assertTrue(sampleList[index] == null)
            } else {
                assertTrue(sampleList[index]?.i == sample.i)
            }
        }
        //removes the first value in the previous observation which is the last value = null
        sampleObserver.previousObservations.removeAt(0)
        sampleObserver.previousObservations.forEachIndexed { index, sample ->
            if (sample == null) {
                assertTrue(sampleList[index] == null)
            } else {
                assertTrue(sampleList[index]?.i == sample.i)
            }
        }
    }

    @Test
    fun that_null_is_emitted_on_subscription() {
        nullableStringSubject.emit(null)
        nullableStringSubject.add(nullableObserver)

        assertTrue(nullableObserver.observations.size == 1)
    }
}