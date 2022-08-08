package com.example.sleeptracker.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.sleeptracker.App
import com.example.sleeptracker.background.androidservices.SurveyService
import com.example.sleeptracker.background.androidservices.TrackerService
import java.util.*

class AlarmReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.applicationContext?.let {
            ContextCompat.startForegroundService(
                it,
                Intent(context.applicationContext, TrackerService::class.java)
            )
        }
    }
}