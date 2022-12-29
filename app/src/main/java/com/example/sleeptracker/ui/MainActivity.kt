package com.example.sleeptracker.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.sleeptracker.BuildConfig
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.utils.DBParameters.CONSENT_ACCEPTED
import com.example.sleeptracker.initAws
import com.example.sleeptracker.models.UserModel
import com.example.sleeptracker.models.getNonNullUserValue
import com.example.sleeptracker.ui.signin.LoginActivity
import org.json.JSONObject


class MainActivity : AppCompatActivity()  {

    companion object {
        var TEST = BuildConfig.DEBUG
        const val TAG  = "MainActivity"
        var c  = "0000012"
    }

    val user : UserModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAws(this){
            AWS.uid {
                val uid = it
                Log.d(TAG, "onCreate: $uid")
                if (uid == null) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d(TAG, "onCreate: $uid")
                    getNonNullUserValue {
                        val u = it
                        try {
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
                        } catch (e: Exception) {
                            val intent = Intent(this, ConsentActivity::class.java)
                            startActivity(intent)
                        }
                        finish()
                    }
                }
            }

        }
    }
}

