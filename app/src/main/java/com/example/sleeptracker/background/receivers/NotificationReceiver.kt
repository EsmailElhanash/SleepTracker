package com.example.sleeptracker.background.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amplifyframework.core.Amplify
import com.example.sleeptracker.database.DBAccessPoint
import com.example.sleeptracker.database.utils.DBParameters
import com.example.sleeptracker.ui.survey.SurveyActivity
import com.example.sleeptracker.utils.androidutils.NotificationType
import com.example.sleeptracker.utils.androidutils.NotificationsManager
import com.example.sleeptracker.utils.time.TimeUtil
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?:return
        val id = 13132
        val action = intent?.action
        if(action =="no"){
            val intentF = Intent(context.applicationContext, NotificationReceiver::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context.applicationContext, id, intentF, 0)
            val n = NotificationsManager.createNotification(
                pendingIntent, context, "Thanks",
                NotificationType.GENERAL
            )
            with(NotificationManagerCompat.from(context.applicationContext)) {
                n?.build()?.let { notify(id, it) }
            }

            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                with(NotificationManagerCompat.from(context.applicationContext)) {
                    n?.build()?.let { cancel(id) }
                }
             }
            val uid = Amplify.Auth.currentUser?.userId ?: return
             DBAccessPoint.getUserSurveyLastConditionTwo(uid).setValue(
                 Calendar.getInstance().timeInMillis
             )
            DBAccessPoint.getUserSurveyConditionTwo(uid).
                child(TimeUtil.getDateSimple()).updateChildren(
                mapOf(
                    DBParameters.TOOK_SURVEY to "no"
                )
            )

        }else if(action == "yes"){
            showSurveyConditionTwoNotification(context.applicationContext)
            val uid = Amplify.Auth.currentUser?.userId ?: return
            DBAccessPoint.getUserSurveyLastConditionTwo(uid).setValue(
                Calendar.getInstance().timeInMillis
            )
            DBAccessPoint.getUserSurveyConditionTwo(uid).
            child(TimeUtil.getDateSimple()).updateChildren(
                mapOf(
                    DBParameters.RESPONSE to "yes"
                )
            )
        }
    }

    private fun showSurveyConditionTwoNotification(context : Context) {
        try {
            val id = 13132
            val title = null
            val text = "Would you like to participate in our survey?"
            val intent = Intent(context.applicationContext, NotificationReceiver::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context.applicationContext, id, intent, 0)
            val notification = NotificationsManager.createNotification(
                pendingIntent, context, title,
                NotificationType.SLEEP_DISTURBANCES, getActionsTwo(context),text
            )
            notification?.let {
                notification.setAutoCancel(false)
                with(NotificationManagerCompat.from(context.applicationContext)) {
                    notify(id, it.build())
                }
            }
        }catch (e:Exception){}
    }

    private fun getActionsTwo(context: Context): List<NotificationCompat.Action> {
        val action1Id = 144
        val actionIntent = Intent(context.applicationContext, SurveyActivity::class.java)
        actionIntent.action = "yes"
        val actionPIntent = PendingIntent.getActivity(context.applicationContext, action1Id, actionIntent, 0)
        val action1 = NotificationCompat
            .Action
            .Builder(null,"yes",actionPIntent)

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