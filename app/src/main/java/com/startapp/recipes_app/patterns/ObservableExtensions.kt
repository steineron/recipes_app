package com.startapp.recipes_app.patterns


/**
 * two extension function to observe/disregard observables
 */

fun <T> Observer<T>.observe(observable: Observable<T>): Unit {
    observable.add(this)
}

fun <T> Observer<T>.disregard(observable: Observable<T>): Unit {
    observable.remove(this)
}


