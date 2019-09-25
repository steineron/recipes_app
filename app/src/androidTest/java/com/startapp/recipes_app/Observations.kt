package com.startapp.recipes_app

import com.startapp.recipes_app.patterns.Observable
import com.startapp.recipes_app.patterns.Observer


// capturer for observations in tests
class Observations<T> : Observer<T> {


    val observations: MutableList<T?> = mutableListOf()

    override fun observed(value: T?, oldValue: T?, observable: Observable<T>) {
        observations.add(value)
    }
}