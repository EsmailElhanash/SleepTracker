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
import com.example.sleeptracker.R
import com.example.sleeptracker.background.MySensorsManager
import com.example.sleeptracker.background.receivers.StateReceiver
import com.example.sleeptracker.initAws
import com.example.sleeptracker.models.SleepPeriod
import com.example.sleeptracker.models.UserModel
import com.example.sleeptracker.models.UserObject
import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.ui.statistics.daily.HOUR_IN_MS
import com.example.sleeptracker.utils.MINUTE_IN_MS
import com.example.sleeptracker.utils.androidutils.NotificationType
import com.example.sleeptracker.utils.androidutils.NotificationsManager
import java.util.*


private const val NOTIFICATION_ID = 1213

class TrackerService : Service(){
    private var wakeLockPeriod = 0L
    private var stateReceiver: BroadcastReceiver? = null
    private var wakeLock : PowerManager.WakeLock? = null
    private var foreGroundNotification: Notification? = null
    private var sensorsManager: MySensorsManager? = null

    private val PERIODIC_CHECKER_FLAG = "PERIODIC_CHECKER_FLAG"

    private lateinit var user: UserModel

    companion object{
        private const val TAG = "TrackerService"
        private var tracking : Boolean = false
        private var activePeriod: SleepPeriod? = null
        fun getActivePeriod(): SleepPeriod? {
            return activePeriod
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initAws(this){
            user = UserModel()
            checkPeriod(intent)
        }
        return START_STICKY
    }


    private fun checkPeriod(intent: Intent?){
        if (!tracking) startForegroundPeriodCheck()
        var inPeriod = false
        UserObject.getSleepPeriodCallBack { periods ->
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

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startForegroundPeriodCheck(){
        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(applicationContext, 145, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getActivity(applicationContext, 145, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
                } else {
                    PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                }
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

    private fun startTracking(period: Period){
        Log.d(TAG, "startTracking:${period.getBasicID()}")
        periodicChecker()
        startForegroundTracking()
        tracking = true

        activePeriod = SleepPeriod(period, getState())

        val periodLength = period.periodEndMS - period.periodStartMS + 2 * HOUR_IN_MS
        wakeLockPeriod = periodLength
        setWakeLock(applicationContext, wakeLockPeriod)

        sensorsManager = MySensorsManager(activePeriod!!)
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
                    intent,PendingIntent.FLAG_UPDATE_CURRENT)
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
                    intent,PendingIntent.FLAG_UPDATE_CURRENT)
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
        Log.d(TAG, "stopTracking:${activePeriod?.period?.getBasicID()}")
        if (activePeriod==null) {
            cancelPeriodicChecker()
            stopForeground()
            return
        }
        if (intent?.getBooleanExtra(PERIODIC_CHECKER_FLAG,false) == true) return
        cancelPeriodicChecker()
        try {
            unregisterReceiver(stateReceiver)
        }catch (_: Exception){}

        val uid = Amplify.Auth.currentUser?.userId
        val pid = activePeriod?.pid
        if (pid != null && uid != null) {
            activePeriod?.saveState("User present")
        }
        sensorsManager?.unRegisterSensors(applicationContext)
        activePeriod?.calculateSessions{
            activePeriod?.calculateSleepDuration(it) {
                activePeriod?.calculateAverageMovementCount {
                    Intent(applicationContext, SurveyService::class.java).also { intent ->
                        ContextCompat.startForegroundService(applicationContext,intent)
                        activePeriod = null
                        tracking = false
                        releaseWakeLock()
                        stopForeground()
                        stopSelf()
                    }

                }
            }
        }
    }

    private fun stopForeground(){
        stopForeground(true)
        foreGroundNotification = null
    }

    private fun releaseWakeLock(){
        try {
            wakeLock?.release()
            Log.d(TAG, "setWakeLock: wakelock released")
        }catch (e:Exception){}
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}