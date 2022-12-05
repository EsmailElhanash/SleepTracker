package com.example.sleeptracker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T : Any> LiveData<T>.getLiveDataValueOnce(observer: (T) -> Unit) {
    value?.let {
        observer(it)
    } ?: observeForever(object: Observer<T> {
        override fun onChanged(value: T) {
            observer(value)
            removeObserver(this)
        }

    })
}