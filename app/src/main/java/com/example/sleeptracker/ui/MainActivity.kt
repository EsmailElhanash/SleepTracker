package com.example.sleeptracker.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.DataStoreConfiguration
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.BuildConfig
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.aws.initAws
import com.example.sleeptracker.database.utils.DBParameters.CONSENT_ACCEPTED
import com.example.sleeptracker.ui.signin.LoginActivity
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity()  {



//    private lateinit var auth: FirebaseAuth

    companion object {
        var TEST = BuildConfig.DEBUG
        const val TAG  = "MainActivity"
        const val c  = 211
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        initAws{
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
                    if (u.consent == CONSENT_ACCEPTED){
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }else {
                        val intent = Intent(this, ConsentActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                }
            }
        }
    }
}

