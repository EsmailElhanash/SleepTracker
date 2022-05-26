package com.example.sleeptracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sleeptracker.R
import com.example.sleeptracker.models.UserModel
import com.example.sleeptracker.database.utils.DBParameters.CONSENT_ACCEPTED
import com.example.sleeptracker.database.utils.DBParameters.CONSENT_DECLINED
import com.example.sleeptracker.databinding.ActivityConsentBinding
import com.example.sleeptracker.ui.survey.SurveyActivity

class ConsentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConsentBinding
    private var progressView : View? = null
    private val user : UserModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.acceptConsent.setOnClickListener {
            showProgressIndicator()
            user.updateConsent(CONSENT_ACCEPTED,{
                runOnUiThread{ showThanksDialog() }
            },{
                onFailure("error occurred")
            })
        }

        binding.declineConsent.setOnClickListener {
            showProgressIndicator()
            user.updateConsent(CONSENT_DECLINED,{
                finishAffinity()
            },{
                onFailure("error occurred")
            })
        }
    }

    private fun showThanksDialog(){
        AlertDialog.Builder(this)
            .setMessage(R.string.thanks_for_participating_text)
            .setOnDismissListener {

            }
            .setOnCancelListener {

            }
            .setPositiveButton(R.string.ok){ _, _->
                val i = Intent(this, SurveyActivity::class.java)
                i.putExtra(SurveyActivity.SURVEY_CASE_EXTRA,SurveyActivity.SURVEY_CASE_1)
                startActivity(i)
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun onFailure(message: String){
        runOnUiThread {
            binding.root.visibility = View.VISIBLE
            progressView?.visibility = View.GONE
            Toast.makeText(
                this, message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showProgressIndicator() {
        binding.root.visibility = View.GONE
        progressView = LayoutInflater.from(this)
            .inflate(R.layout.progress_view, binding.root as ViewGroup, false)

        progressView?.findViewById<TextView>(R.id.progressText)?.text = "Loading"
        val myDialog = AlertDialog.Builder(this)
            .setView(progressView)
            .setOnDismissListener {
            }
            .setOnCancelListener {

            }
            .create()

        myDialog.show()
    }
}