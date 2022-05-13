package com.example.sleeptracker.ui.signin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.example.sleeptracker.R
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.databinding.ActivityLoginBinding
import com.example.sleeptracker.ui.HomeActivity
import com.example.sleeptracker.ui.MainActivity.Companion.TEST
import com.example.sleeptracker.ui.survey.SurveyActivity
import com.example.sleeptracker.utils.androidutils.DialogManager
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var root : View
    private var myDialog: AlertDialog? = null


    @SuppressLint("all")
    private fun setTest(){
        binding.emailInputLogin.editText?.setText("esmailelhanash${SignUpActivity.c}@gmail.com")
        binding.passwordInputLogin.editText?.setText("eeee1111")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        root = binding.root
        setContentView(root)
//        val auth = Firebase.auth
        if (TEST) setTest()
        binding.loginButton.setOnClickListener {
            try {
                Amplify.Auth.signIn(
                    binding.emailInputLogin.editText?.text.toString(),
                    binding.passwordInputLogin.editText?.text.toString(),
                    { result ->
                        if (result.isSignInComplete) {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            Log.i("AuthQuickstart", "Sign in succeeded")
                        } else {
                            onFailure("Sign in not complete")
                            Log.i("AuthQuickstart", "Sign in not complete")
                        }
                    },
                    {
                        it.message?.let { it1 -> onFailure(it1) }
                    }
                )
                }catch (e:Exception){
                Toast.makeText(this, R.string.incorrect_data,Toast.LENGTH_SHORT).show()
            }
        }

        binding.signUpInstead.setOnClickListener{
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
//            finish()
        }
    }
    private fun onFailure(message: String){
        runOnUiThread {
            Toast.makeText(
                this, message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun showAlertMessage(errorText: String, context: Context) {
        myDialog = AlertDialog.Builder(context)
            .setMessage(errorText)
            .setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .setOnDismissListener {
//                        root.visibility = View.VISIBLE
                myDialog = null
            }
            .setOnCancelListener {
//                        root.visibility = View.VISIBLE
                myDialog = null
            }
            .create()
        myDialog?.show()
    }
}