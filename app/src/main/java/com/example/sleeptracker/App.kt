package com.example.sleeptracker

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager


class App : Application() {
    companion object{
        fun restart (context: Context){
            val packageManager: PackageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            val componentName = intent?.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            context.startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }
    }
}