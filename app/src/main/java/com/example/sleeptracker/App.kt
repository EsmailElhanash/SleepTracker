package com.example.sleeptracker

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.IntentCompat


class App : Application() {

    companion object{
        @SuppressLint("UnspecifiedImmutableFlag")
        lateinit var INSTANCE: App
        fun restart (context: Context){
            val mainIntent =
                IntentCompat.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_LAUNCHER)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.applicationContext.startActivity(mainIntent)
            System.exit(0)
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this@App
    }
}