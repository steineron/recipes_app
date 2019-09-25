package com.startapp.recipes_app.patterns


/**
* The most simple abstraction of an observable.
*
* Use this abstraction to build interfaces and APIs for your components without coupling them with
* specific implementations such as RxJava and LiveData.
*
* by doing so - you will be able to achieve SOLID's Dependency Inversion principle.
*
* you can easily implement this interface using implementations from LiveData, RxJava and Bus (to name a few)
*
* */
interface Observable<T> {

    fun add(observer: Observer<T>)

    fun remove(observer: Observer<T>)

    fun removeAllObservers()
}