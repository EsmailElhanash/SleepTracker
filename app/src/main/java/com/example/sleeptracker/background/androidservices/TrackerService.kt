package com.example.sleeptracker.background.androidservices

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.DayGroup
import com.example.sleeptracker.R
import com.example.sleeptracker.background.MySensorsManager
import com.example.sleeptracker.background.receivers.StateReceiver
import com.example.sleeptracker.utils.DBParameters
import com.example.sleeptracker.initAws
import com.example.sleeptracker.models.SleepPeriod
import com.example.sleeptracker.models.getNonNullUserValue
import com.example.sleeptracker.objects.TimePeriod
import com.example.sleeptracker.objects.TimePoint
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.ui.statistics.daily.HOUR_IN_MS
import com.example.sleeptracker.utils.MINUTE_IN_MS
import com.example.sleeptracker.utils.androidutils.NotificationType
import com.example.sleeptracker.utils.androidutils.NotificationsManager
import com.example.sleeptracker.utils.time.DAY_IN_MS
import com.example.sleeptracker.utils.time.TimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import com.amplifyframework.kotlin.core.Amplify as AmplifyKT


private const val NOTIFICATION_ID = 1213

class TrackerService : Service(){
    private var wakeLockPeriod = 0L
    private var stateReceiver: BroadcastReceiver? = null
    private var wakeLock : PowerManager.WakeLock? = null
    private var foreGroundNotification: Notification? = null
    private var sensorsManager: MySensorsManager? = null

    private val PERIODIC_CHECKER_FLAG = "PERIODIC_CHECKER_FLAG"

    companion object{
        private const val TAG = "TrackerService"
        private var tracking : Boolean = false
        private var activeSleepPeriod: SleepPeriod? = null
        fun getActivePeriod(): SleepPeriod? {
            return activeSleepPeriod
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        checkPeriod(intent)

        return START_STICKY
    }


    private fun checkPeriod(intent: Intent?){
        if (!tracking) startForegroundPeriodCheck()
        initAws(this){
            var inPeriod = false
            getSleepPeriodCallBack { periods ->
                periods.forEach {
                    if (Calendar.getInstance().timeInMillis in it.periodStartMS..it.periodEndMS) {
                        Log.d("TrackerService", "checkPeriod: Active period:${it.getBasicID()}")
                        if (!tracking)
                            startTracking(it)
                        inPeriod = true
                        return@forEach
                    }
                }
                if (!inPeriod)
                    stopTracking(intent)
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startForegroundPeriodCheck(){
        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(applicationContext, 145, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getActivity(applicationContext, 145, notificationIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT } else { PendingIntent.FLAG_UPDATE_CURRENT })
            }
        }
        foreGroundNotification = NotificationsManager.createNotification(
            pendingIntent,
            applicationContext,
            getText(R.string.checking_time),
            NotificationType.CHECK_PERIOD
        )?.build()
        startForeground(
            NOTIFICATION_ID, foreGroundNotification
        )
    }


    private fun startForegroundTracking(){

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }
        foreGroundNotification = NotificationsManager.createNotification(
            pendingIntent,
            applicationContext,
            getText(R.string.app_is_active),
            NotificationType.TRACKING
        )?.build()
        startForeground(
            NOTIFICATION_ID, foreGroundNotification
        )
        Log.d(TAG, "started foreground")
    }

    private fun startTracking(timePeriod: TimePeriod){
        Log.d(TAG, "startTracking:${timePeriod.getBasicID()}")
        periodicChecker()
        startForegroundTracking()
        tracking = true

        CoroutineScope(Dispatchers.Main).launch{
            activeSleepPeriod = SleepPeriod(
                timePeriod, getState(),AmplifyKT.Auth.getCurrentUser().userId
            )
        }

        val periodLength = timePeriod.periodEndMS - timePeriod.periodStartMS + 2 * HOUR_IN_MS
        wakeLockPeriod = periodLength
        setWakeLock(applicationContext, wakeLockPeriod)

        sensorsManager = MySensorsManager(activeSleepPeriod!!)
        sensorsManager!!.startSensors(
            this,
        )

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        if (stateReceiver == null) {
            stateReceiver = StateReceiver()
            registerReceiver(stateReceiver, intentFilter)
        }
        Intent(applicationContext, AlarmService::class.java).also {
            ContextCompat.startForegroundService(applicationContext,it)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun periodicChecker(){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pIntent = Intent(applicationContext, TrackerService::class.java).let { intent ->
            intent.putExtra(PERIODIC_CHECKER_FLAG,true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getService(applicationContext,
                    5555,
                    intent, PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getService(applicationContext,
                    5555,
                    intent,if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT } else { PendingIntent.FLAG_UPDATE_CURRENT })
            }
        }
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            5 * MINUTE_IN_MS,
            5 * MINUTE_IN_MS,
            pIntent
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelPeriodicChecker(){
        val pIntent = Intent(applicationContext, TrackerService::class.java).let { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getService(applicationContext,
                    5555,
                    intent, PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getService(applicationContext,
                    5555,
                    intent,if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT } else { PendingIntent.FLAG_UPDATE_CURRENT })
            }
        }
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.cancel(pIntent)
    }

    private fun getState():String {
        val pm = getSystemService(POWER_SERVICE) as PowerManager?
        val isOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            pm?.isInteractive ?: false
        } else {
            pm?.isScreenOn ?: false
        }
        return if (isOn) "User present" else "off"
    }

    private fun setWakeLock(context: Context, wakeLockPeriod:Long){
        Log.d(TAG, "setWakeLock: acquiring for ${wakeLockPeriod/1000/60} minutes")
        wakeLock = (context.getSystemService(POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                acquire(wakeLockPeriod)
                Log.d(TAG, "setWakeLock: wakelock acquired for ${wakeLockPeriod/1000/60} minutes")
            }
        }
    }

    private fun stopTracking(intent: Intent? = null){
        Log.d(TAG, "stopTracking:${activeSleepPeriod?.timePeriod?.getBasicID()}")
        if (activeSleepPeriod==null) {
            cancelPeriodicChecker()
            stopForeground()
            return
        }
        if (intent?.getBooleanExtra(PERIODIC_CHECKER_FLAG,false) == true) return
        cancelPeriodicChecker()
        try {
            unregisterReceiver(stateReceiver)
        }catch (_: Exception){}

        Amplify.Auth.getCurrentUser({
            val pid = activeSleepPeriod?.pid
            val uid = it.userId
            if (pid != null && uid != null) {
                activeSleepPeriod?.saveState("User present")
            }
            sensorsManager?.unRegisterSensors(applicationContext)
            activeSleepPeriod?.calculateSessions{
                activeSleepPeriod?.calculateSleepDuration(it) {
                    activeSleepPeriod?.calculateAverageMovementCount {
                        Intent(applicationContext, SurveyService::class.java).also { intent ->
                            ContextCompat.startForegroundService(applicationContext,intent)
                            activeSleepPeriod = null
                            tracking = false
                            releaseWakeLock()
                            stopForeground()
                            stopSelf()
                        }

                    }
                }
            }
        },{})

    }

    private fun getSleepPeriodCallBack(callback: (timePeriods: List<TimePeriod>) -> Unit) {
        getNonNullUserValue {
            val wd = it.workday
            val od = it.offDay

            val possibleActiveTimePeriods: ArrayList<TimePeriod> = arrayListOf()
            val nowDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) % 7
            val yesterday = Calendar.getInstance().let { cal->
                cal.time = Date(cal.timeInMillis - DAY_IN_MS)
                cal.get(Calendar.DAY_OF_WEEK) % 7
            }
            if (wd != null && od != null) {
                getPossibleActivePeriods(wd,od,nowDayOfWeek)?.let { it1 -> possibleActiveTimePeriods.add(it1) }
                getPossibleActivePeriods(wd,od,yesterday)?.let { it1 -> possibleActiveTimePeriods.add(it1) }
            }
            callback(possibleActiveTimePeriods)
        }
    }

    private fun getPossibleActivePeriods(workDaysGroup: DayGroup, offDaysGroup: DayGroup, day:Int): TimePeriod?{
        val dayName = DBParameters.DAYS[day]
        val sleepTimePoint : TimePoint
        val awakeTimePoint : TimePoint
        when (dayName) {
            in workDaysGroup.days -> {
                sleepTimePoint = TimePoint.stringToObject(workDaysGroup.sleepTime) ?: return null
                awakeTimePoint = TimePoint.stringToObject(workDaysGroup.wakeUpTime) ?: return null
            }
            in offDaysGroup.days -> {
                sleepTimePoint = TimePoint.stringToObject(offDaysGroup.sleepTime) ?: return null
                awakeTimePoint = TimePoint.stringToObject(offDaysGroup.wakeUpTime) ?: return null
            }
            else -> return null
        }
        val timePair = TimeUtil.getStartEndPair(
            sleepTimePoint,
            awakeTimePoint,
            Calendar.getInstance().timeInMillis
        )
        return TimePeriod(timePair.first, timePair.second)
    }

    private fun stopForeground(){
        stopForeground(true)
        foreGroundNotification = null
    }

    private fun releaseWakeLock(){
        try {
            wakeLock?.release()
            Log.d(TAG, "setWakeLock: wakelock released")
        }catch (_:Exception){}
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}