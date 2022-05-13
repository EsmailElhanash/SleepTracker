package com.example.sleeptracker.utils.androidutils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.sleeptracker.R
import com.example.sleeptracker.background.receivers.NotificationReceiver
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.ui.survey.SurveyActivity


private const val GENERAL_CHANNEL_ID = "Sleep Tracker General Channel"
private const val CHECK_PERIOD_CHANNEL_ID = "Period Check"
private const val SURVEY_CHANNEL_ID = "Survey Notification"
private const val TRACKING_ACTIVE_CHANNEL_ID = "Active Tracking"
private const val UPDATING_DATA_CHANNEL_ID = "Updating Data"
private const val SLEEP_DISTURBANCES_CHANNEL_ID = "Sleep Disturbances"

enum class NotificationType{
    CHECK_PERIOD,TRACKING,UPDATING_DATA,TAKE_SURVEY,SLEEP_DISTURBANCES,GENERAL
}

object NotificationsManager {
    @Suppress("DEPRECATION")
    fun createNotification(pendingIntent: PendingIntent
                           , context : Context
                           , title:CharSequence?
                           , type:NotificationType, actions:List<NotificationCompat.Action>? = null
                           , message:CharSequence? = null): NotificationCompat.Builder? {
        val channelID = when (type){
            NotificationType.CHECK_PERIOD -> CHECK_PERIOD_CHANNEL_ID
            NotificationType.TRACKING -> TRACKING_ACTIVE_CHANNEL_ID
            NotificationType.UPDATING_DATA -> UPDATING_DATA_CHANNEL_ID
            NotificationType.TAKE_SURVEY -> SURVEY_CHANNEL_ID
            NotificationType.SLEEP_DISTURBANCES -> SLEEP_DISTURBANCES_CHANNEL_ID
            else -> {GENERAL_CHANNEL_ID}
        }
        try {
            val n = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val imp = if (actions?.isNotEmpty() == true) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_LOW
                NotificationCompat.Builder(context,
                    createNotificationChannel(channelID,context,imp)
                )
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(NotificationCompat.BigTextStyle())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.notification)
            }else{
                NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(NotificationCompat.BigTextStyle())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
            }
            actions?.forEach {
                n.addAction(it)
            }
            return n
        }catch (e:Exception){
            return null
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, context : Context,imp:Int): String {
        val channel = NotificationChannel(channelId,
            channelId, imp)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    @Suppress("DEPRECATION")
    fun displaySurveyNotification(context : Context) {
        try {
            val id = 1010
            val intent = Intent(context.applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context.applicationContext, 0, intent, 0)
            val notification = createNotification(pendingIntent,context,context.getString(R.string.retake_the_survey),
                NotificationType.TAKE_SURVEY
            )
            notification?.let {
                with(NotificationManagerCompat.from(context.applicationContext)) {
                    notify(id, it.build())
                }
            }
        }catch (e:Exception){}
    }

    fun showSurveyConditionTwoNotification(context : Context) {
        try {
            val id = 13132
            val title = null
            val text = "Do you have any sleep disturbance(s) over the past few nights?"
            val intent = Intent(context.applicationContext, NotificationReceiver::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context.applicationContext, id, intent, 0)
            val notification = createNotification(pendingIntent,context,title,
                NotificationType.SLEEP_DISTURBANCES, getActionsOne(context),text
            )
            notification?.let {
                notification.setAutoCancel(false)
                with(NotificationManagerCompat.from(context.applicationContext)) {
                    notify(id, it.build())
                }
            }
        }catch (e:Exception){}
    }

    private fun getActionsOne(context: Context): List<NotificationCompat.Action> {
        val action1Id = 144
        val actionIntent = Intent(context.applicationContext, NotificationReceiver::class.java)
        actionIntent.action = "yes"
        val actionPIntent = PendingIntent.getBroadcast(context.applicationContext, action1Id, actionIntent, 0)
        val action1 = NotificationCompat
            .Action
            .Builder(null,"Yes",actionPIntent)

        val action2Id = 146
        val action2Intent = Intent(context.applicationContext, NotificationReceiver::class.java)
        action2Intent.action = "no"
        val action2PIntent = PendingIntent.getBroadcast(context.applicationContext, action2Id, action2Intent, 0)
        val action2 = NotificationCompat
            .Action
            .Builder(null,"No",action2PIntent)
        return listOf(action1.build(),action2.build())
    }
}