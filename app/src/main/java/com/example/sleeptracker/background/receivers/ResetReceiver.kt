package com.example.sleeptracker.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.sleeptracker.App
import com.example.sleeptracker.background.androidservices.AlarmService
import com.example.sleeptracker.background.androidservices.SurveyService
import com.example.sleeptracker.background.androidservices.TrackerService

class ResetReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val appCtx = context.applicationContext
            if (Intent.ACTION_BOOT_COMPLETED == intent?.action ||
                    Intent.ACTION_LOCKED_BOOT_COMPLETED == intent?.action||
                    Intent.ACTION_TIMEZONE_CHANGED == intent?.action||
                    "android.intent.action.TIME_SET" == intent?.action||
                    Intent.ACTION_TIME_CHANGED == intent?.action) {
                ContextCompat.startForegroundService(context.applicationContext,Intent(context.applicationContext, AlarmService::class.java))
                ContextCompat.startForegroundService(context.applicationContext,Intent(context.applicationContext, SurveyService::class.java))
            }
        }

    }
}