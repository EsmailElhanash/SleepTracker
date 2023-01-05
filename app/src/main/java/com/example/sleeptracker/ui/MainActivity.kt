package com.example.sleeptracker.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.BuildConfig
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.background.androidservices.AlarmService
import com.example.sleeptracker.database.utils.DBParameters.CONSENT_ACCEPTED
import com.example.sleeptracker.initAws
import com.example.sleeptracker.ui.signin.LoginActivity
import org.json.JSONObject


class MainActivity : AppCompatActivity()  {

    companion object {
        var TEST = BuildConfig.DEBUG
        const val TAG  = "MainActivity"
        const val c  = 11711
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAws(applicationContext as AlarmService) {
            val uid = Amplify.Auth?.currentUser?.userId
            Log.d(TAG, "onCreate: $uid")
            if (uid==null){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                Log.d(TAG, "onCreate: $uid")
                AWS.get(uid,User::class.java){
                    Log.d(TAG, "onCreate: ${it.success}")
                    val u = it.data as User
                    try{
                        JSONObject(u.consent).get(
                            "consent"
                        ).let { c ->
                            if (c == CONSENT_ACCEPTED) {
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                            } else {
                                val intent = Intent(this, ConsentActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }catch (e:Exception) {
                        val intent = Intent(this, ConsentActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                }
            }
        }
    }
}

