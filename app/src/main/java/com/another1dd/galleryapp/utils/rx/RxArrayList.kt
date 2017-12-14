package com.another1dd.galleryapp.utils.rx

import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject


class RxArrayList<T> : ArrayList<T>() {
    var notifier = ReplaySubject.create<RxArrayList<T>>()

    fun asObservable(): Observable<RxArrayList<T>> {
        return notifier
    }

    override fun add(element: T): Boolean {
        notifier.onNext(this)
        return super.add(element)
    }

    override fun remove(element: T): Boolean {
        notifier.onNext(this)
        return super.remove(element)
    }

    override fun removeAt(index: Int): T {
        notifier.onNext(this)
        return super.removeAt(index)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        notifier.onNext(this)
        return super.removeAll(elements)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        notifier.onNext(this)
        return super.addAll(elements)
    }

    override fun add(index: Int, element: T) {
        notifier.onNext(this)
        super.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        notifier.onNext(this)
        return super.addAll(index, elements)
    }
}
