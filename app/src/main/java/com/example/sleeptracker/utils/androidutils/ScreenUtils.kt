package com.example.sleeptracker.utils.androidutils

import android.content.res.Resources


object ScreenUtils {
    fun getWidth():Int{
        return Resources.getSystem().displayMetrics.widthPixels
    }
    fun getHeight():Int{
        return Resources.getSystem().displayMetrics.heightPixels
    }
}