package com.example.sleeptracker

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.DataStoreConfiguration
import com.amplifyframework.datastore.DataStoreConflictHandler
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.background.androidservices.AlarmService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.util.concurrent.TimeUnit
import com.amplifyframework.kotlin.core.Amplify as AmplifyKT


class App : Application() {

    companion object{
        lateinit var INSTANCE : Application
        @SuppressLint("UnspecifiedImmutableFlag")
        fun restart (context: Context){
            val mainIntent =
                IntentCompat.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_LAUNCHER)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.applicationContext.startActivity(mainIntent)
            System.exit(0)
        }
        fun crash(){
            Toast.makeText(null, "Crashed before shown.", Toast.LENGTH_SHORT).show();
        }
    }
    override fun onCreate() {
        INSTANCE = this
        handleExceptions()
        super.onCreate()
    }


    private fun handleExceptions() {
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            suspend {
                AmplifyKT.Auth.getCurrentUser().userId.apply {
                    Intent(applicationContext,AlarmService::class.java).also {
                        ContextCompat.startForegroundService(applicationContext,it)
                    }
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }

            }

        }
    }
}

fun initAws (context: Context,onComplete:()-> Unit){
    Log.d("APP1 package name = ", App.INSTANCE.packageName)
    Log.d("APP1 package context = ", context.toString())
    try{
        val datastorePlugin = AWSDataStorePlugin.builder().run {
            dataStoreConfiguration(
                DataStoreConfiguration.builder()
                    .doSyncRetry(true)
                    .syncInterval(5,TimeUnit.MINUTES)
                    .syncPageSize(50)
                    .conflictHandler { c, onDecision ->
                        Log.d("datastore init", "conflict: $c")
                        onDecision.accept(DataStoreConflictHandler.ConflictResolutionDecision.retryLocal())
                    }
                    .errorHandler {
                        Log.d("datastore init", "exception: $it")
                        FirebaseCrashlytics.getInstance().log("Datastore exception: $it")
                        AWS.amplifyRetry()
                    }
                    .build()
            ).build()
        }
        Amplify.addPlugin(datastorePlugin)
        Amplify.addPlugin(AWSApiPlugin())

        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.configure(
            AmplifyConfiguration.fromConfigFile(context,R.raw.amplifyconfiguration),context
        )
        AWS.hub()
        onComplete()
    }catch (e: Exception){
        if (e is Amplify.AlreadyConfiguredException)
            onComplete()
        Log.d("amplify conf exception", "exception: $e")

    }
}