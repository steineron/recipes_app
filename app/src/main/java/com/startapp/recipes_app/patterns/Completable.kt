package com.startapp.recipes_app.patterns

/**
 * an instance of this can complete of err - report completion or error of a task/flow
 */
interface Complete {
    fun complete()
    fun error(throwable: Throwable)
}


/**
 * an observer for completion of completable
 */
interface Completion {
    fun onComplete()
    fun onError(throwable: Throwable)
}

/**
 * a observable that reports completion or error of a task/flow to instances of Completion
 */
interface Completable {
    fun add(observer: Completion)
    fun remove(observer: Completion)
    fun removeAllObservers()
}


/**
 * an implementation of a Completable and Complete - observe with Completion instances, call complete/error to finalise the flow/task
 */
class Completeness : Completable, Complete {

    // a subject emitting nullable errors:
    // calling `complete()` will emit null via this subject
    // call error(error) will emit error via this subject
    private val status = BehaviourSubject<Throwable?>()


    // an inner observer to the status subject that wraps a Completion observer
    // calls `onComplete` when null is observed
    // call `onError(e)` when non-null error e is observed
    private inner class ObserverWrapper(val observer: Completion) : Observer<Throwable?> {
        override fun observed(value: Throwable?, oldValue: Throwable?, observable: Observable<Throwable?>) {
            when (value) {
                null -> observer.onComplete()
                else -> observer.onError(value)
            }
            disregard(observable)
        }
    }


    // completing or reporting an error once nad only once - using this boolean to track that
    private var completed: Boolean = false // true if completed or erred already

    // mapping Completion to ObserverWrapper to support add/remove of Completion instances
    private val wrappedObservers: HashMap<Completion, ObserverWrapper> = HashMap()

    override fun complete() {
        if (!completed) {
            status.emit(null)
            completed = true
        }
    }

    override fun error(throwable: Throwable) {
        if (!completed) {
            status.emit(throwable)
            completed = true
        }
    }

    override fun add(observer: Completion) {
        if (wrappedObservers.containsKey(observer))
            return

        ObserverWrapper(observer).also {
            it.observe(status)
            wrappedObservers[observer] = it
        }
    }

    override fun remove(observer: Completion) {

        wrappedObservers[observer]?.let {
            it.disregard(status)
            wrappedObservers.remove(it.observer)
        }
    }

    override fun removeAllObservers() {
        status.removeAllObservers()
        wrappedObservers.clear()
    }


}

