package com.example.sleeptracker.background.receivers

import android.content.*
import com.amplifyframework.core.Amplify
import com.example.sleeptracker.background.androidservices.TrackerService


import java.util.*

class StateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val uid = Amplify.Auth.currentUser?.userId ?: return
        val appCtx = context?.applicationContext
        val period = TrackerService.getActivePeriod()
        val pid = period?.id
        pid?.let{
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
            val i = Intent(appCtx, TrackerService::class.java)
            appCtx.startService(i)
        }else {
            activePeriod.saveState("User present")
        }

    }
}