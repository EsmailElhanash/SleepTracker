package com.example.sleeptracker.background.receivers

import android.content.*
import android.os.Build
import androidx.core.content.ContextCompat
import com.amplifyframework.core.Amplify
import com.example.sleeptracker.App
import com.example.sleeptracker.background.androidservices.SurveyService
import com.example.sleeptracker.background.androidservices.TrackerService


import java.util.*

class StateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val appCtx = context?.applicationContext
        val period = TrackerService.getActivePeriod()
        period?.let{
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    period.saveState("off")
                }
                Intent.ACTION_SCREEN_ON -> {
                    period.saveState("on")
                }
                Intent.ACTION_USER_PRESENT -> {
                    appCtx?.let {checkAwakeTime(appCtx)}
                }
                else -> {}
            }
        }

    }
    private fun checkAwakeTime(appCtx:Context){
        val activePeriod = TrackerService.getActivePeriod()
        val p = activePeriod?.period ?: return

        val nowMS = Calendar.getInstance().timeInMillis
        val periodStart = p.periodStartMS
        val periodEnd = p.periodEndMS
        if(nowMS !in periodStart..periodEnd){
            val i = Intent(appCtx.applicationContext, TrackerService::class.java)
            ContextCompat.startForegroundService(appCtx.applicationContext,i)
        }else {
            activePeriod.saveState("User present")
        }

    }
}