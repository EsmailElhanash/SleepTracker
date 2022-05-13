package com.example.userdataapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class App2Service : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent==null) {
            stopSelf()
            return START_NOT_STICKY
        }
        val req = intent.getStringExtra(REQUEST)
        if (req==null){
            stopSelf()
            return START_NOT_STICKY
        }

        when (req) {
            SIGN_UP -> {
                val signUpService = Intent(applicationContext,SignUpService::class.java)
                signUpService.putExtras(intent)
                startService(signUpService)
            }
            SIGN_UP_RESULT -> {
                val errors = intent.getStringExtra(SIGN_UP_ERROR)
                val sid = intent.getStringExtra(SID)
                val resultIntent = Intent(SIGN_UP_RESULT)
                if (errors!=null){ // fail sign up
                    resultIntent.putExtra(SIGN_UP_RESULT,false)
                    resultIntent.putExtra(SIGN_UP_ERROR,errors)
                }else if (sid != null){ // successful sign up
                    resultIntent.putExtra(SIGN_UP_RESULT,true)
                    resultIntent.putExtra(SID,sid)
                }

                LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent)
            }
        }

//
//
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val REQUEST = "REQUEST" // MANDATORY EXTRA NAME, OR I'App2Service' WILL NOT HANDLE YOUR REQUEST

        //POSSIBLE VALUES
        const val SIGN_UP = "SIGN_UP" // EXTRA VALUE
        const val SIGN_UP_RESULT = "SIGN_UP_RESULT"// EXTRA VALUE, AND A NAME FOR A BOOLEAN EXTRA
//        const val SAVE_CONSENT_RESPONSE = "SAVE_CONSENT_RESPONSE" // EXTRA VALUE OF A REQUEST TO SAVE THE CONSENT RESPONSE
//        const val SAVE_CONSENT_RESULT = "SAVE_CONSENT_RESULT" // EXTRA VALUE OF THE RESULT OF A REQUEST TO SAVE THE CONSENT RESPONSE
//        const val LOAD_CONSENT_STATE = "LOAD_CONSENT_RESPONSE" // EXTRA VALUE OF A REQUEST TO RETRIEVE THE CONSENT SAVED RESPONSE


        //MORE EXTRA NAMES
        const val SIGN_UP_ERROR = "SIGN_UP_ERROR"//NAME FOR ERROR STRING EXTRA
        const val SID = "SID" //NAME FOR SID VALUE OF A SUCCESSFUL SIGNUP
//        const val CONSENT_RESPONSE = "CONSENT" //NAME FOR A BOOLEAN EXTRA OF THE CONSENT RESPONSE OF ACCEPTANCE OR DECLINING
    }
}