package com.startapp.recipes_app.patterns

/**
 * the most simple abstraction of an emitter for values of type T.
 *
 * combine with an Observable to create a working unit that can emit to observers
 */

interface Emitter<T> {

    fun emit(value: T)
}