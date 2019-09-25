package com.startapp.recipes_app.patterns

import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test


/**
 * basicallly test that Completeness only completes once and reports completion/error correctly
 */
class CompletenessTest {

    private class TestComplete : Completion {
        var completes: Int = 0
        val errors: MutableList<Throwable> = mutableListOf()

        override fun onComplete() {
            completes++
        }

        override fun onError(throwable: Throwable) {
            errors.add(throwable)
        }

    }

    lateinit var completeness: Completeness

    @Before
    fun setUp() {
        completeness = Completeness()
    }

    @After
    fun tearDown() {
    }


    @Test
    fun that_observing_completion_of_incomplete_does_nothing() {
        // setup

        // execute
        val test = TestComplete().also {
            it.observe(completeness)

        }
        // verify
        assertTrue(test.errors.isEmpty())
        assertTrue(test.completes == 0)
    }

    @Test
    fun that_observing_completion_of_completed_reports_complete_immediately() {
        // setup

        completeness.complete()

        // execute
        val test = TestComplete().also {

            assertTrue(it.errors.isEmpty())
            assertTrue(it.completes == 0)

            // now observe to receive the complete
            it.observe(completeness)

        }
        // verify
        assertTrue(test.errors.isEmpty())
        assertTrue(test.completes == 1)
    }


    @Test
    fun that_observing_completion_of_error_reports_error_immediately() {
        // setup

        completeness.error(NullPointerException())

        // execute
        val test = TestComplete().also {
            assertTrue(it.errors.isEmpty())
            assertTrue(it.completes == 0)

            // now observe to receive the error
            it.observe(completeness)

        }
        // verify
        assertTrue(test.errors.size ==1)
        assertTrue(test.completes == 0)
    }


    @Test
    fun that_a_call_to_complete_result_in_a_single_invocation_of_onComplete() {
        // setup

         val test = TestComplete().also {
            it.observe(completeness)
             assertTrue(it.errors.isEmpty())
             assertTrue(it.completes == 0)
        }
        // execute
        completeness.complete()

        // verify
        assertTrue(test.errors.isEmpty())
        assertTrue(test.completes == 1)
    }
    @Test
    fun that_a_call_to_error_result_in_a_single_invocation_of_onError() {
        // setup

         val test = TestComplete().also {
            it.observe(completeness)
             assertTrue(it.errors.isEmpty())
             assertTrue(it.completes == 0)
        }
        // execute
        completeness.error(NullPointerException())

        // verify
        assertTrue(test.errors.size ==1)
        assertTrue(test.completes == 0)
    }


    @Test
    fun that_multiple_calls_to_complete_result_in_a_single_invocation_of_onComplete() {
        // setup

         val test = TestComplete().also {
            it.observe(completeness)
             assertTrue(it.errors.isEmpty())
             assertTrue(it.completes == 0)
        }
        // execute
        completeness.complete()
        completeness.complete()
        completeness.complete()
        completeness.complete()
        completeness.complete()
        completeness.complete()

        // verify
        assertTrue(test.errors.isEmpty())
        assertTrue(test.completes == 1)
    }

    @Test
    fun that_mixing_multiple_calls_to_complete_then_error_result_in_a_single_invocation_of_onComplete() {
        // setup

         val test = TestComplete().also {
            it.observe(completeness)
             assertTrue(it.errors.isEmpty())
             assertTrue(it.completes == 0)
        }
        // execute
        completeness.complete()
        completeness.complete()
        completeness.error(NullPointerException())
        completeness.complete()
        completeness.complete()
        completeness.error(NullPointerException())
        completeness.complete()
        completeness.error(NullPointerException())

        // verify
        assertTrue(test.errors.isEmpty())
        assertTrue(test.completes == 1)
    }
    @Test
    fun that_multiple_calls_to_error_result_in_a_single_invocation_of_onError() {
        // setup

         val test = TestComplete().also {
            it.observe(completeness)
             assertTrue(it.errors.isEmpty())
             assertTrue(it.completes == 0)
        }
        // execute
        completeness.error(NullPointerException())
        completeness.error(NullPointerException())
        completeness.error(NullPointerException())
        completeness.error(NullPointerException())
        completeness.error(NullPointerException())
        completeness.error(NullPointerException())
        completeness.error(NullPointerException())
        completeness.error(NullPointerException())


        // verify
        assertTrue(test.errors.size == 1)
        assertTrue(test.completes == 0)
    }

    @Test
    fun that_mixing_multiple_calls_to_error_then_complete_result_in_a_single_invocation_of_onError() {
        // setup

         val test = TestComplete().also {
            it.observe(completeness)
             assertTrue(it.errors.isEmpty())
             assertTrue(it.completes == 0)
        }
        // execute
        completeness.error(NullPointerException())
        completeness.complete()
        completeness.complete()
        completeness.complete()
        completeness.error(NullPointerException())
        completeness.complete()
        completeness.error(NullPointerException())
        completeness.complete()

        // verify
        assertTrue(test.errors.size == 1)
        assertTrue(test.completes == 0)
    }


    @Test
    fun that_multiple_observers_of_completion_receive_completion() {
        // setup - create 5 completions - and observe the completeness

        val completions = mutableListOf<TestComplete>()
        for (i in 0 until 5) {
            completions.add(TestComplete().also {
                it.observe(completeness)

                // verify
                assertTrue(it.errors.isEmpty())

                assertTrue(it.completes == 0)
            })

        }

        // execute - complete
        completeness.complete()

        //verify - each completion has 1 complete, 0 errors
        completions.forEach {
            assertTrue(it.errors.isEmpty())

            assertTrue(it.completes == 1)
        }

    }



    @Test
    fun that_removed_observers_of_completion_DO_NOT_receive_completion() {
        // setup - create 5 completions, remove 2 - and observe the completeness

        val completions = mutableListOf<TestComplete>()
        for (i in 0 until 5) {
            completions.add(TestComplete().also {
                it.observe(completeness)

                // verify
                assertTrue(it.errors.isEmpty())
                assertTrue(it.completes == 0)
            })

        }

        // remove two observers
        val removed = mutableListOf<TestComplete>()

        removed.add(completions.removeAt(4).also { it.disregard(completeness) })
        removed.add(completions.removeAt(3).also { it.disregard(completeness) })

        // execute - complete
        completeness.complete()

        //verify - each completion has 1 complete, 0 errors
        completions.forEach {
            assertTrue(it.errors.isEmpty())

            assertTrue(it.completes == 1)
        }
        // verify each removed still has 0 completions
        removed.forEach {
            assertTrue(it.errors.isEmpty())

            assertTrue(it.completes == 0)
        }

    }


    @Test
    fun that_removed_observers_of_error_DO_NOT_receive_error() {
        // setup - create 5 completions - and observe the errors

        val errors = mutableListOf<TestComplete>()
        for (i in 0 until 5) {
            errors.add(TestComplete().also {
                it.observe(completeness)

                // verify
                assertTrue(it.errors.isEmpty())
                assertTrue(it.completes == 0)
            })

        }

        // remove two observers
        val removed = mutableListOf<TestComplete>()

        removed.add(errors.removeAt(4).also { it.disregard(completeness) })
        removed.add(errors.removeAt(3).also { it.disregard(completeness) })

        // execute - complete
        completeness.error(NullPointerException())

        //verify - each completion has 1 complete, 0 errors
        errors.forEach {
            assertTrue(it.errors.size==1)

            assertTrue(it.completes == 0)
        }
        // verify each removed still has 0 completions
        removed.forEach {
            assertTrue(it.errors.isEmpty())

            assertTrue(it.completes == 0)
        }

    }
}