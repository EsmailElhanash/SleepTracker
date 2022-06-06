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
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.TrackerPeriod
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.DB
import com.example.sleeptracker.database.utils.DBParameters.CONSENT_ACCEPTED
import com.example.sleeptracker.objects.Period
import com.example.sleeptracker.ui.signin.LoginActivity
import com.example.sleeptracker.ui.signin.SignUpActivity
import com.example.sleeptracker.ui.survey.SurveyActivity


class MainActivity : AppCompatActivity()  {



//    private lateinit var auth: FirebaseAuth

    companion object {
        const val TEST = true

        const val TAG  = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        configureAWS {
            val uid = Amplify.Auth?.currentUser?.userId


            Log.d(TAG, "onCreate: $uid")
            if (uid==null){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                Log.d(TAG, "onCreate: $uid")
                DB.get(uid,User::class.java){
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

    private fun configureAWS(onSuccess : ()->Unit) {
        try{
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(AmplifyConfiguration.fromConfigFile(applicationContext,R.raw.amplifyconfiguration),applicationContext)
            onSuccess()
        }catch (e: AmplifyException){
            if (e is Amplify.AlreadyConfiguredException) {
                onSuccess()
                return
            }
            Toast.makeText(this,"Error Occurred" , Toast.LENGTH_SHORT).show()
        }
    }
}

