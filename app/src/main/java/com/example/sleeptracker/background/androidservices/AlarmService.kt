package com.example.sleeptracker.background.androidservices

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.sleeptracker.App
import com.example.sleeptracker.R
import com.example.sleeptracker.background.receivers.AlarmReceiver
import com.example.sleeptracker.database.utils.DBParameters.DAYS
import com.example.sleeptracker.initAws
import com.example.sleeptracker.models.UserModel
import com.example.sleeptracker.objects.DaysGroup
import com.example.sleeptracker.objects.TimePoint
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.utils.MINUTE_IN_MS
import com.example.sleeptracker.utils.androidutils.NotificationType
import com.example.sleeptracker.utils.androidutils.NotificationsManager
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class AlarmService : Service() {
    private var isForeground: Boolean = false
    private lateinit var user: UserModel
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initAws(this) {
            user = UserModel()
            if (!isForeground) prepareAlarmStartTime()
            else {
                stopForeground()
                prepareAlarmStartTime()
            }
        }
        return START_NOT_STICKY
    }

    private fun prepareAlarmStartTime() {
        goForeGround()
        var workDaysGroup : DaysGroup? = null
        var offDaysGroup : DaysGroup? = null
        user.workDays.observeForever{
            if (it != null) {
                workDaysGroup = it
                if (workDaysGroup!=null && offDaysGroup!=null)
                    setAlarm(workDaysGroup!!, offDaysGroup!!)

            }
        }
        user.offDays.observeForever {
            if (it != null) {
                offDaysGroup = it
                if (workDaysGroup!=null && offDaysGroup!=null)
                    setAlarm(workDaysGroup!!, offDaysGroup!!)
            }
        }
        CoroutineScope(Dispatchers.IO).launch{
            delay(60000)
            if (isForeground) stopForeground()
        }
    }


    private fun setAlarm(workDaysGroup: DaysGroup,offDaysGroup: DaysGroup){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        if (alarmManager != null) {
            //Day of week , Saturday = 0 ... Friday = 6
            val nowDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) % 7
            DAYS.forEachIndexed { dayNum, dayName ->
                val sleepTimePoint : TimePoint
                val awakeTimePoint : TimePoint
                when (dayName) {
                    in workDaysGroup.daysNames -> {
                        sleepTimePoint = workDaysGroup.sleepTime
                        awakeTimePoint = workDaysGroup.wakeTime
                    }
                    in offDaysGroup.daysNames -> {
                        sleepTimePoint = offDaysGroup.sleepTime
                        awakeTimePoint = workDaysGroup.wakeTime
                    }
                    else -> return
                }
                val sleepAndAwake = TimeUtil
                    .getStartEndPair(sleepTimePoint,awakeTimePoint, Calendar.getInstance().timeInMillis)

                val nxtSleepTime = sleepAndAwake.first + MINUTE_IN_MS
                val dayDiff = (7 - (nowDayOfWeek - dayNum)) % 7
                val startTimeMS = (nxtSleepTime + dayDiff * DAY_IN_MS)
                val pIntent = Intent(applicationContext, AlarmReceiver::class.java).let { intent ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.getBroadcast(applicationContext,
                            dayNum,
                            intent,PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
                    } else {
                        PendingIntent.getBroadcast(applicationContext,
                            dayNum,
                            intent,PendingIntent.FLAG_UPDATE_CURRENT)
                    }
                }
                Log.d("setAlarm ", ": ${Date(startTimeMS)}")
                when {
                    Build.VERSION.SDK_INT >= 23 -> {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, startTimeMS, pIntent
                        )
                    }
                    Build.VERSION.SDK_INT >= 19 -> {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTimeMS, pIntent)
                    }
                    else -> {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, startTimeMS, pIntent)
                    }
                }
            }
        }
        ContextCompat.startForegroundService(applicationContext,
            Intent(applicationContext, TrackerService::class.java)
        )
        stopForeground()
    }






    private fun stopForeground(){
        stopForeground(true)
        isForeground = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun goForeGround(){
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java)
                .let { notificationIntent ->
                    PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)
                }
        startForeground(11114, NotificationsManager.createNotification(
            pendingIntent,applicationContext,getText(R.string.updating_data),NotificationType.UPDATING_DATA
        )?.build()
        )
        isForeground = true
    }

}