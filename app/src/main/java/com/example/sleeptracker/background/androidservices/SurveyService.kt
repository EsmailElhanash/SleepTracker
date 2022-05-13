package com.example.sleeptracker.background.androidservices

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.amplifyframework.core.Amplify
import com.example.sleeptracker.R
import com.example.sleeptracker.background.receivers.SurveyAlarmReceiver
import com.example.sleeptracker.database.DBAccessPoint
import com.example.sleeptracker.models.SurveysViewModel
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.utils.LAST_SURVEY_NOTIFICATION
import com.example.sleeptracker.utils.PREFERENCES_NAME
import com.example.sleeptracker.utils.androidutils.NotificationType
import com.example.sleeptracker.utils.androidutils.NotificationsManager
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import com.google.firebase.ktx.Firebase
import java.util.*

const val SURVEY_ALARM_ID = 12321
private const val DISPLAYABLE_DAYS_COUNT = 7

class SurveyService : Service() {

    private var isForeground: Boolean = false
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        goForeGround()
        scheduleSurveyCheck()
        checkSurveyConditionOne{
            checkSurveyConditionTwo {
                stopForeground()
            }
        }

        return START_NOT_STICKY
    }
    private fun checkSurveyConditionOne(onComplete: (() -> Unit)){
        val nowMS = Calendar.getInstance().timeInMillis
        val pref = applicationContext.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val lastNotification = pref.getLong(LAST_SURVEY_NOTIFICATION, 0)
        if (nowMS<=lastNotification + DAY_IN_MS){
            onComplete()
            return
        }
        SurveysViewModel().surveyLastUpdated
            .observeForever{
                try {
                    if (it != null) {
                        if (nowMS>=(it+28* DAY_IN_MS)){
                            NotificationsManager.displaySurveyNotification(applicationContext)
                            pref.edit().putLong(LAST_SURVEY_NOTIFICATION, nowMS).apply()
                        }
                    }
                    onComplete()
                } catch (e: Exception) {onComplete()}
            }
    }

    private fun checkSurveyConditionTwo(onComplete: () -> Unit){
        val uid = Amplify.Auth.currentUser?.userId ?: return

        val nowMS = Calendar.getInstance().timeInMillis
        var s1 : Long? = null
        var s2 : Long? = null
        var loadPeriodsData = {}
        val checkDate = {c1:Long , c2:Long ->
            if (nowMS > c1 + 7 * DAY_IN_MS && nowMS > c2 + 28 * DAY_IN_MS){
                loadPeriodsData()
            }else{
                onComplete()
            }
         }
        SurveysViewModel().surveyLastUpdated
            .observeForever{
                s1 = it?: 0
                if (s1!=null && s2!=null)checkDate(s1!!, s2!!)
            }
        SurveysViewModel().surveyLastShownCase2
            .observeForever{
                s2 = it?: 0
                if (s1!=null && s2!=null)checkDate(s1!!, s2!!)
            }

        val periodsIDs = getPeriodsIDs()
        var checkCondition = {}

        val sortedMovementCount :SortedMap<Long,Int> = sortedMapOf()
        val sortedDisturbancesCount :SortedMap<Long,Int> = sortedMapOf()
        loadPeriodsData = {
            DBAccessPoint.getTrackerDirectory(uid)
                .orderByKey()
                .startAt(periodsIDs.last())
                .endAt("${periodsIDs.first()}, 23:59")
                .get().addOnSuccessListener { snapshot ->
                    val shots = snapshot.children.toList()
                    shots.forEach {
                        val pid = it.key ?:return@addOnSuccessListener
                        val ms = TimeUtil.pidToMS(pid)
                        try {
                            val count = it.child("Disturbances Count").value.toString().toInt()
                            sortedDisturbancesCount[ms]=count
                        }catch (e:Exception){}

                        try {
                            val count = it.child("Movement Count").value.toString().toInt()
                            sortedMovementCount[ms]=count
                        }catch (e:Exception){}
                    }
                    checkCondition()
                }.addOnFailureListener{
                    onComplete()
                }
        }
        checkCondition = {
            val movementCountList = arrayListOf<Int>()
            val disturbancesCountList = arrayListOf<Int>()
            var shouldShowConditionTwoNotification = false
            for ((_, count) in sortedMovementCount) {
                movementCountList.add(count)
                val lastIndex = movementCountList.size - 1
                if (lastIndex >= 4) {
                    shouldShowConditionTwoNotification =
                        movementCountList[lastIndex] >=3 &&
                                movementCountList[lastIndex - 1] >=3 &&
                                +movementCountList[lastIndex - 2] >=3 &&
                                movementCountList[lastIndex - 3] >=3 &&
                                movementCountList[lastIndex - 4] >=3
                }
            }

            for ((_, count) in sortedDisturbancesCount) {
                disturbancesCountList.add(count)
                val lastIndex = disturbancesCountList.size - 1
                if (lastIndex >= 4) {
                    val last5Count = disturbancesCountList[lastIndex] +
                            disturbancesCountList[lastIndex - 1] +
                            +disturbancesCountList[lastIndex - 2] +
                            disturbancesCountList[lastIndex - 3] +
                            disturbancesCountList[lastIndex - 4]
                    if (last5Count >= 3) shouldShowConditionTwoNotification = true
                }
            }
            if (shouldShowConditionTwoNotification)
                NotificationsManager.showSurveyConditionTwoNotification(this)
            onComplete()
        }
    }

    private fun getPeriodsIDs() : Array<String> {
        val nowMS = Calendar.getInstance().timeInMillis - DAY_IN_MS
        return Array(DISPLAYABLE_DAYS_COUNT) {
            TimeUtil.getIDSimpleFormat(nowMS - (it * DAY_IN_MS))
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun scheduleSurveyCheck(){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,12)
        calendar.set(Calendar.MINUTE,0)
        print(calendar.time)
        val pIntent = Intent(applicationContext, SurveyAlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(applicationContext,
                SURVEY_ALARM_ID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        when {
            Build.VERSION.SDK_INT >= 23 -> {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis, DAY_IN_MS ,pIntent
                )
            }
            Build.VERSION.SDK_INT >= 19 -> {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, DAY_IN_MS,pIntent)
            }
            else -> {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, DAY_IN_MS,pIntent)
            }
        }
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
                    PendingIntent.getActivity(this, 0, notificationIntent, 0)
                }
        startForeground(131,NotificationsManager.createNotification(
            pendingIntent,applicationContext,getText(R.string.checking_survey), NotificationType.UPDATING_DATA
        )?.build())
        isForeground = true
    }
}