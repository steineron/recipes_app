package com.startapp.recipes_app.patterns


/**
 * The most simple abstraction of an observer.
 *
 * Couple this abstraction with <tt>Observable</tt> to achieve SOLID's Dependency Inversion principle.
 *
 * you can easily implement this interface using implementations from LiveData, RxJava and Bus (to name a few)
 *
 * @see au.com.domain.util.Observable
 * */
interface Observer<T> {

    fun observed(value: T?, oldValue: T?, observable: Observable<T>)
}