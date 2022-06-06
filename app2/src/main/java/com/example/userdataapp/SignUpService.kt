package com.example.userdataapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignInOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.datastore.generated.model.User2


class SignUpService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?: return START_NOT_STICKY
        val name = intent.getStringExtra(SignUpStrings.NAME)
        val email = intent.getStringExtra(SignUpStrings.EMAIL)
        val sid = intent.getStringExtra(SignUpStrings.SID)
        val age = intent.getStringExtra(SignUpStrings.AGE)
        val ethnic = intent.getStringExtra(SignUpStrings.ETHNIC)
        val gender = intent.getStringExtra(SignUpStrings.GENDER)
        val pw = intent.getStringExtra(SignUpStrings.PASSWORD)

        if (name==null ||
            email==null ||
            age==null ||
            ethnic==null ||
            gender==null ||
            sid==null||
            pw==null) return START_NOT_STICKY

        configureAws{
            val options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email)
                .build()
            val options2 = AuthSignInOptions.defaults()

            Amplify.Auth.signUp(email, pw, options,
                let@ {signUpResult->
                    val id = signUpResult.user?.userId
                    if (id==null){
                        onFailure("Sign up error occurred")
                        return@let
                    }
                    Amplify.Auth.signIn(email,pw,options2, {
                        if (it.isSignInComplete) {
                            val u = User2.builder().gender(gender)
                                .age(age.toInt())
                                .email(email)
                                .ethnic(ethnic)
                                .name(name)
                                .sid(sid)
                                .build()

                            Amplify.API.mutate(
                                ModelMutation.create(u),
                                {
                                    onSuccess(id)
                                }, { exc ->
                                    exc.localizedMessage?.let { it1 -> onFailure(it1) } ?: onFailure("error occurred")
                                }
                                )
                        }else{
                            onFailure("Sign in error occurred")
                        }
                    }, {
                        it.localizedMessage?.let { it1 -> onFailure(it1) } ?: onFailure("error occurred")
                        }
                    )

                },{
                    it.localizedMessage?.let { it1 -> onFailure(it1) } ?: onFailure("error occurred")
                }
            )
        }
        return START_STICKY
    }

    private fun onFailure(error:String){
        val intent = Intent(applicationContext,App2Service::class.java)

        intent.putExtra(App2Service.REQUEST,App2Service.SIGN_UP_RESULT)
        intent.putExtra(App2Service.SIGN_UP_RESULT,false)
        intent.putExtra(App2Service.SIGN_UP_ERROR,error)

        startService(intent)
        stopSelf()
    }

    private fun onSuccess(sid:String){
        val intent = Intent(applicationContext,App2Service::class.java)

        intent.putExtra(App2Service.REQUEST,App2Service.SIGN_UP_RESULT)
        intent.putExtra(App2Service.SIGN_UP_RESULT,true)
        intent.putExtra(App2Service.SID,sid)

        startService(intent)
        stopSelf()
    }

    private fun configureAws(onSuccess:()-> Unit){
        try{
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(AmplifyConfiguration.fromConfigFile(applicationContext, R.raw.amplifyconfiguration2),applicationContext)
            Log.d("configureAws", "succ")
            onSuccess()
        }catch (e: AmplifyException){
            Log.d("configureAws", "configureAws: ${e.message}")
            Toast.makeText(this,"Error Occurred" , Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}