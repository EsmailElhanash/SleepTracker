package com.example.sleeptracker.background.androidworker

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.sleeptracker.background.androidservices.AlarmService
import com.example.sleeptracker.background.androidservices.TrackerService
import com.example.sleeptracker.background.receivers.AlarmReceiver
import com.example.sleeptracker.database.utils.DBParameters
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


class AlarmWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private var isForeground: Boolean = false
    private lateinit var user: UserModel
    override fun doWork(): Result {
        initAws(applicationContext as AlarmService) {
            user = UserModel()
            if (!isForeground) prepareAlarmStartTime()
            else {
                prepareAlarmStartTime()
            }
        }
        return Result.success()
    }

    private fun prepareAlarmStartTime() {
        setForegroundAsync(createForeGroundInfo())
        var workDaysGroup: DaysGroup? = null
        var offDaysGroup: DaysGroup? = null
        user.workDays.observeForever {
            if (it != null) {
                workDaysGroup = it
                if (workDaysGroup != null && offDaysGroup != null)
                    setAlarm(workDaysGroup!!, offDaysGroup!!)

            }
        }
        user.offDays.observeForever {
            if (it != null) {
                offDaysGroup = it
                if (workDaysGroup != null && offDaysGroup != null)
                    setAlarm(workDaysGroup!!, offDaysGroup!!)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(60000)
            //  if (isForeground) stopForeground()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setAlarm(workDaysGroup: DaysGroup, offDaysGroup: DaysGroup) {
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarmManager != null) {
            //Day of week , Saturday = 0 ... Friday = 6
            val nowDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) % 7
            DBParameters.DAYS.forEachIndexed { dayNum, dayName ->
                val sleepTimePoint: TimePoint
                val awakeTimePoint: TimePoint
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
                    .getStartEndPair(
                        sleepTimePoint,
                        awakeTimePoint,
                        Calendar.getInstance().timeInMillis
                    )

                val nxtSleepTime = sleepAndAwake.first + MINUTE_IN_MS
                val dayDiff = (7 - (nowDayOfWeek - dayNum)) % 7
                val startTimeMS = (nxtSleepTime + dayDiff * DAY_IN_MS)
                val pIntent = Intent(applicationContext, AlarmReceiver::class.java).let { intent ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.getBroadcast(
                            applicationContext,
                            dayNum,
                            intent, PendingIntent.FLAG_IMMUTABLE
                        )
                    } else {
                        PendingIntent.getBroadcast(
                            applicationContext,
                            dayNum,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT
                        )
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
        ContextCompat.startForegroundService(
            applicationContext,
            Intent(applicationContext, TrackerService::class.java)
        )
        //   stopForeground()
    }

    private fun createForeGroundInfo(): ForegroundInfo {
        val pendingIntent: PendingIntent =
            Intent(applicationContext, MainActivity::class.java)
                .let { notificationIntent ->
                    PendingIntent.getActivity(
                        applicationContext, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }
        val notification: Notification? = NotificationsManager.createNotification(
            pendingIntent, applicationContext, "Updating data", NotificationType.UPDATING_DATA
        )?.build()
        isForeground = true
        return ForegroundInfo(1, notification!!)
    }
}