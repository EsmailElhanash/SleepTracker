package com.example.sleeptracker.background.androidservices

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.TrackerPeriod
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.background.receivers.SurveyAlarmReceiver
import com.example.sleeptracker.initAws
import com.example.sleeptracker.models.UserModel
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.utils.LAST_SURVEY_NOTIFICATION
import com.example.sleeptracker.utils.PREFERENCES_NAME
import com.example.sleeptracker.utils.androidutils.NotificationType
import com.example.sleeptracker.utils.androidutils.NotificationsManager
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

const val SURVEY_ALARM_ID = 12321

class SurveyService : Service() {

    private var isForeground: Boolean = false
    private lateinit var user : UserModel
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initAws(this){
            user = UserModel()
            goForeGround()
            scheduleSurveyCheck()
            CoroutineScope(Dispatchers.IO).launch {
                checkSurveyConditionOne {
                    when (it) {
                        28 -> checkSurveyConditionTwo {
                            stopForeground()
                        }
                        else -> stopForeground()
                    }
                }
                delay(60000)
                if (isForeground) stopForeground()
            }
        }

        return START_NOT_STICKY
    }
    private fun checkSurveyConditionOne(onComplete: ((retake: Int) -> Unit)){
        val nowMS = Calendar.getInstance().timeInMillis
        val pref = applicationContext.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val lastNotification = pref.getLong(LAST_SURVEY_NOTIFICATION, 0)
        if (nowMS<=lastNotification + DAY_IN_MS){
            user.getSurveyRetakePeriod {
                onComplete(it)
            }
            return
        }

        user.getSurveyLastUpdatedCaseOne{ last1->
            user.getSurveyRetakePeriod { retake->
                try {
                    if (nowMS>=(last1+retake* DAY_IN_MS)){
                        NotificationsManager.displaySurveyNotification(applicationContext)
                        pref.edit().putLong(LAST_SURVEY_NOTIFICATION, nowMS).apply()
                    }
                    onComplete(retake)
                } catch (e: Exception) {onComplete(retake)}
            }
        }

    }

    private fun checkSurveyConditionTwo(onComplete: () -> Unit){
        val nowMS = Calendar.getInstance().timeInMillis
        var s1 : Long? = null
        var s2 : Long? = null

        val sortedMovementCount :SortedMap<Long,Int> = sortedMapOf()
        val sortedDisturbancesCount :SortedMap<Long,Int> = sortedMapOf()


        val checkCondition = {
            val movementCountList = arrayListOf<Int>()
            val disturbancesCountList = arrayListOf<Int>()
            var shouldShowConditionTwoNotification = false
            for ((_, count) in sortedMovementCount) {
                movementCountList.add(count)
                val lastIndex = movementCountList.size - 1
                if (lastIndex >= 4) {
                    shouldShowConditionTwoNotification =
                        movementCountList[lastIndex    ] >=3 &&
                        movementCountList[lastIndex - 1] >=3 &&
                        movementCountList[lastIndex - 2] >=3 &&
                        movementCountList[lastIndex - 3] >=3 &&
                        movementCountList[lastIndex - 4] >=3
                }
            }

            for ((_, count) in sortedDisturbancesCount) {
                disturbancesCountList.add(count)
                val lastIndex = disturbancesCountList.size - 1
                if (lastIndex >= 4) { // todo CRITICAL CHANGE BACK TO 4 NOT 0
                    val last5Count =
                            disturbancesCountList[lastIndex    ] +
                            disturbancesCountList[lastIndex - 1] +
                            disturbancesCountList[lastIndex - 2] +
                            disturbancesCountList[lastIndex - 3] +
                            disturbancesCountList[lastIndex - 4]
                    if (last5Count >= 3) shouldShowConditionTwoNotification = true
                }
            }
            if (shouldShowConditionTwoNotification) {// todo CRITICAL CHANGE BACK TO shouldShowConditionTwoNotification NOT !shouldShowConditionTwoNotification
                NotificationsManager.showSurveyConditionTwoNotification(this)
                onComplete()
            }else {
                onComplete()
            }

        }

        val checkDate = { c1:Long , c2:Long ->
            if (nowMS > c1 + 7 * DAY_IN_MS && nowMS > c2 + 28 * DAY_IN_MS) { // TODO CHANGE BACK first 0 TO 7 second 0 to 28
                getPeriodsData{ periods ->
                    periods?.forEach let@{ model ->
                        if (model !is TrackerPeriod) return@let
                        val ms = model.createdAt?.toDate()?.time
                        try {
                            val count = model.disturbancesCount.toInt()
                            sortedDisturbancesCount[ms]=count
                        }catch (e:Exception){}

                        try {
                            val count = model.averageMovementCount.toInt()
                            sortedMovementCount[ms]=count
                        }catch (e:Exception){}
                    }

                    checkCondition()
            }
            } else {
                onComplete()
            }
         }

        user.getSurveyLastUpdatedCaseOne{
            s1 = it
            if (s1!=null && s2!=null)checkDate(s1!!, s2!!)
        }


        user.getSurveyLastUpdatedCaseTwo{
            s2 = it
            if (s1!=null && s2!=null)checkDate(s1!!, s2!!)
        }
    }

    private fun getPeriodsData(onComplete: (trackerPeriods: List<Model>?) -> Unit){
        val periodsIDs = getPeriodsIDs()
        var predicates : QueryPredicate = Where.matches(TrackerPeriod.ID.beginsWith(periodsIDs.first())).queryPredicate

        periodsIDs.forEach { pid ->
            predicates = predicates.or(TrackerPeriod.ID.beginsWith(pid))
        }

        AWS.getPredicate(
            predicates,
            TrackerPeriod::class.java
        ){
            onComplete(it.data)
        }
    }

    private fun getPeriodsIDs() : Array<String> {
        val nowMS = Calendar.getInstance().timeInMillis - DAY_IN_MS
        return Array(7) {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(applicationContext,
                    SURVEY_ALARM_ID,
                    intent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getBroadcast(applicationContext,
                    SURVEY_ALARM_ID,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.getActivity(this, 0, notificationIntent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    } else {
                        PendingIntent.getActivity(this, 0, notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
                }
        startForeground(131,NotificationsManager.createNotification(
            pendingIntent,applicationContext,getText(R.string.checking_survey), NotificationType.UPDATING_DATA
        )?.build())
        isForeground = true
    }
}