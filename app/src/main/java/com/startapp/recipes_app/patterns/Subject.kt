package com.startapp.recipes_app.patterns

/**
 * A subject is an implementation for Observable and Emitter
 * */
open class Subject<T> : Observable<T>, Emitter<T> {

    /**
     * convert the com.startapp.recipes_app.patterns.Observer to a java.util.Observer by wrapping one
     */
    private inner class ObserverWrapper(val observer: Observer<T>) : java.util.Observer {

        override fun update(o: java.util.Observable?, arg: Any?) {
            val u: T? = arg as? T?
            u.let {
                observer.observed(it, lastValue, this@Subject)
            }
        }
    }

    // required, to make setChanged public/ accessible
    private class ActualObservable : java.util.Observable() {
        public override fun setChanged() {
            super.setChanged()
        }
    }

    /**
     * the actual observable wrapped by this implementation
     */
    private val observable: ActualObservable = ActualObservable()

    /**
     * maps registered observers - via ::add - to the wrapper that uses them
     */
    private val wrappedObservers: HashMap<Observer<T>, ObserverWrapper> = HashMap()

    /**
     * keeps a record of the last-emitted value
     */
    protected var lastValue: T? = null
        private set(value) {
            field = value
        }

    // Overrides/Implementations

    override fun add(observer: Observer<T>) {
        synchronized(this) {
            if (wrappedObservers.containsKey(observer))
                return

            val observerWrapper = ObserverWrapper(observer)
            wrappedObservers[observer] = observerWrapper
            observable.addObserver(observerWrapper)
        }
    }

    override fun removeAllObservers() {
        synchronized(this) {
            observable.deleteObservers()
            wrappedObservers.clear()
        }
    }

    override fun remove(observer: Observer<T>) {
        synchronized(this) {
            val removed: ObserverWrapper? = wrappedObservers.remove(observer)
            removed?.let {
                observable.deleteObserver(it)
            }
        }
    }

    override fun emit(value: T) {
        synchronized(this) {
            observable.setChanged()
            observable.notifyObservers(value)

            lastValue = value
        }
    }
}