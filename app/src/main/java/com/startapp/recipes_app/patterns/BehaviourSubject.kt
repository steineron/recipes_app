package com.startapp.recipes_app.patterns

/**
 * A subject that emits the previously emitted value to any new observer
 * */
class BehaviourSubject<T> : Subject<T>() {

    private var emitted: Boolean = false // true if we emitted something, false otherwise

    override fun add(observer: Observer<T>) {
        synchronized(this) {
            super.add(observer)
            if (emitted) { // emit
                observer.observed(lastValue, null, this@BehaviourSubject)
            }
        }
    }

    override fun emit(value: T) {
        super.emit(value)
        emitted = true
    }

}