package com.example.sleeptracker.background.androidservices

import android.annotation.SuppressLint
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
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.DataStoreConfiguration
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.aws.initAws
import com.example.sleeptracker.background.MySensorsManager
import com.example.sleeptracker.background.receivers.StateReceiver
import com.example.sleeptracker.models.SleepPeriod
import com.example.sleeptracker.models.UserModel
import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.ui.statistics.daily.HOUR_IN_MS
import com.example.sleeptracker.utils.androidutils.NotificationType
import com.example.sleeptracker.utils.androidutils.NotificationsManager
import java.util.*
import java.util.concurrent.TimeUnit


private const val NOTIFICATION_ID = 1213

class TrackerService : Service(){
    private var wakeLockPeriod = 0L
    private var stateReceiver: BroadcastReceiver? = null
    private var wakeLock : PowerManager.WakeLock? = null
    private var foreGroundNotification: Notification? = null
    private var sensorsManager: MySensorsManager? = null

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
        initAws {
            user = UserModel()
            checkPeriod()
        }

        return START_STICKY
    }

    @Synchronized
    private fun checkPeriod(){
        if (!tracking) startForegroundPeriodCheck()
        var inPeriod = false
        user.getSleepPeriodCallBack { periods ->
            periods.forEach {
                if (Calendar.getInstance().timeInMillis in it.periodStartMS .. it.periodEndMS){
                    Log.d("TrackerService", "checkPeriod: Active period:${it.getBasicID()}")
                    if (!tracking)
                        startTracking(it)
                    inPeriod = true
                    return@forEach
                }
             }
             if(!inPeriod)
                 stopTracking()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startForegroundPeriodCheck(){
        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
        foreGroundNotification = NotificationsManager.createNotification(
            pendingIntent,
            this,
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
                    PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
                } else {
                    PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                }
            }
        foreGroundNotification = NotificationsManager.createNotification(
            pendingIntent,
            this,
            getText(R.string.app_is_active),
            NotificationType.TRACKING
        )?.build()
        startForeground(
            NOTIFICATION_ID, foreGroundNotification
        )
    }

    @Synchronized
    private fun startTracking(period: Period){
        Log.d(TAG, "startTracking:${period.getBasicID()}")
        startForegroundTracking()
        tracking = true

        activePeriod = SleepPeriod(period,getState())

        val periodLength = period.periodEndMS - period.periodStartMS + 2 * HOUR_IN_MS
        wakeLockPeriod = periodLength
        setWakeLock(this, wakeLockPeriod)

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
            startService(it)
        }
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
        wakeLock = (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                acquire(wakeLockPeriod)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Intent(applicationContext, SurveyService::class.java).also {
            startService(it)
        }
    }

    @Synchronized
    private fun stopTracking(){
        synchronized(this){
            Log.d(TAG, "stopTracking:${activePeriod?.period?.getBasicID()}")
            if (activePeriod==null) {
                stopForeground()
                return
            }

            try {
                unregisterReceiver(stateReceiver)
            }catch (e: Exception){}

            val uid = Amplify.Auth.currentUser?.userId
            val pid = activePeriod?.pid
            if (pid != null && uid != null) {
                activePeriod?.saveState("User present")
            }

            releaseWakeLock()
            sensorsManager?.unRegisterSensors(applicationContext)
            activePeriod?.calculateSessions{
                activePeriod?.calculateSleepDuration(it) {
                    activePeriod?.calculateAverageMovementCount {
                        activePeriod?.forceSave{
                            activePeriod = null
                            tracking = false
                            stopForeground()
                            stopSelf()
                        }
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
        try { wakeLock?.release()
        }catch (e:Exception){}
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}