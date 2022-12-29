package com.example.sleeptracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sleeptracker.R
import com.example.sleeptracker.aws.AWS
import com.example.sleeptracker.utils.DBParameters.CONSENT_ACCEPTED
import com.example.sleeptracker.utils.DBParameters.CONSENT_DECLINED
import com.example.sleeptracker.databinding.ActivityConsentBinding
import com.example.sleeptracker.models.UserModel
import com.example.sleeptracker.models.getNonNullUserValue
import com.example.sleeptracker.ui.survey.SurveyActivity
import org.json.JSONObject

class ConsentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConsentBinding
    private var progressView : View? = null
    private val userModel : UserModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsentBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.next.setOnClickListener let@{
            if (!isAllQuestionAnswered()) {
                Toast.makeText(this,"please answer all questions",Toast.LENGTH_SHORT).show()
                return@let
            }
            showProgressIndicator()
            getNonNullUserValue{
                val editedUser = it.copyOfBuilder().consent(getConsentAnswer()).build()
                AWS.save(editedUser){}
                if (!isConsentAccepted()){
                    runOnUiThread {
                        showExitDialog()
                    }
                    return@getNonNullUserValue
                }
                runOnUiThread{ showThanksDialog() }

            }

        }
    }

    private fun isConsentAccepted() : Boolean {
        return findViewById<RadioButton>(binding.q1Radio.checkedRadioButtonId).text.toString() == "yes"
                && findViewById<RadioButton>(binding.q2Radio.checkedRadioButtonId).text.toString() == "yes"
                && findViewById<RadioButton>(binding.q3Radio.checkedRadioButtonId).text.toString() == "yes"
                && findViewById<RadioButton>(binding.q4Radio.checkedRadioButtonId).text.toString() == "yes"

    }

    private fun isAllQuestionAnswered() : Boolean =
                    binding.q1Radio.checkedRadioButtonId != -1
                &&  binding.q2Radio.checkedRadioButtonId != -1
                &&  binding.q3Radio.checkedRadioButtonId != -1
                &&  binding.q4Radio.checkedRadioButtonId != -1
                &&  binding.q5Radio.checkedRadioButtonId != -1
                &&  binding.q6Radio.checkedRadioButtonId != -1
                &&  binding.q7Radio.checkedRadioButtonId != -1
                &&  binding.q8Radio.checkedRadioButtonId != -1
                &&  binding.q9Radio.checkedRadioButtonId != -1
                &&  binding.q10Radio.checkedRadioButtonId != -1
                &&  binding.q11Radio.checkedRadioButtonId != -1
                &&  binding.q12Radio.checkedRadioButtonId != -1
                &&  binding.q13Radio.checkedRadioButtonId != -1
                &&  binding.q14Radio.checkedRadioButtonId != -1


    private fun getConsentAnswer(): String {
        return JSONObject().apply {
            val res = if (isConsentAccepted()) CONSENT_ACCEPTED else CONSENT_DECLINED
            put("consent",res)

            findViewById<RadioButton>(binding.q1Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q1Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q2Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q2Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q3Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q3Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q4Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q4Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q5Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q5Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q6Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q6Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q7Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q7Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q8Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q8Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q9Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q9Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q10Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q10Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q11Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q11Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q12Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q12Text.text.toString(),this)
            }

            findViewById<RadioButton>(binding.q13Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q13Text.text.toString(),this)
            }


            findViewById<RadioButton>(binding.q14Radio.checkedRadioButtonId).text.toString().apply {
                put(binding.q14Text.text.toString(),this)
            }
        }.toString()
    }

    private fun showExitDialog(){
        AlertDialog.Builder(this)
            .setTitle("consent declined")
            .setMessage(R.string.exit_dialog)
            .setOnDismissListener {
                finishAffinity()
            }
            .setOnCancelListener {
                finishAffinity()
            }
            .setPositiveButton(android.R.string.ok){ _, _->
                finishAffinity()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun showThanksDialog(){
        AlertDialog.Builder(this)
            .setMessage(R.string.thanks_for_participating_text)
            .setOnDismissListener {

            }
            .setOnCancelListener {

            }
            .setPositiveButton(android.R.string.ok){ _, _->
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
                Toast.LENGTH_SHORT
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