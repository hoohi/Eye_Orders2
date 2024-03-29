package com.eyeorders.util.livedata.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.eyeorders.util.livedata.event.Event
import com.eyeorders.util.livedata.event.EventObserver

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this


fun <T> LiveData<Event<T>>.observeEvent(owner: LifecycleOwner, observer: (T) -> Unit) =
    observe(owner,  EventObserver<T> {
        observer.invoke(it)
    })

infix fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>) =
    Transformations.switchMap(this, func)

infix fun <X, Y> LiveData<X>.map(func: (X) -> LiveData<Y>) = Transformations.map(this, func)

infix fun <X, Y> LiveData<X>.mapFunc(func: (X) -> Y) = Transformations.map(this, func)
