package com.example.sleeptracker.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sleeptracker.background.androidservices.SurveyService
import com.example.sleeptracker.background.androidservices.TrackerService

class SurveyAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val appContext = context?.applicationContext

        appContext?.startService(
            Intent(appContext, SurveyService::class.java)
        )
    }
}