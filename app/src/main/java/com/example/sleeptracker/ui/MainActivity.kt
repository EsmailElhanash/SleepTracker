package com.example.sleeptracker.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.example.sleeptracker.BuildConfig
import com.example.sleeptracker.R
import com.example.sleeptracker.database.utils.DBParameters.CONSENT_ACCEPTED
import com.example.sleeptracker.initAws
import com.example.sleeptracker.models.UserObject
import com.example.sleeptracker.ui.signin.LoginActivity
import com.example.sleeptracker.utils.getLiveDataValueOnce
import org.json.JSONObject


class MainActivity : AppCompatActivity()  {

    companion object {
        var TEST = BuildConfig.DEBUG
        const val TAG  = "MainActivity"
        const val c  = 999900006
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAws(this) {
            val uid = Amplify.Auth?.currentUser?.userId
            Log.d(TAG, "onCreate: $uid")
            if (uid==null){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                Log.d(TAG, "onCreate: $uid")
                UserObject.user.getLiveDataValueOnce {
                    val u = it
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

