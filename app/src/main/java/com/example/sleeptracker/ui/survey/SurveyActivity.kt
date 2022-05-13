package com.example.sleeptracker.ui.survey

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.example.sleeptracker.R
import com.example.sleeptracker.models.SurveysViewModel
import com.example.sleeptracker.databinding.ActivitySurveyBinding
import com.example.sleeptracker.ui.HomeActivity
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.ui.survey.utils.PrepareQuestions
import com.example.sleeptracker.ui.survey.utils.SurveyPage
import com.example.sleeptracker.ui.survey.utils.SurveyQuestion
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

const val anxiety1 = "<a href=\"https://www.youngminds.org.uk/young-person/mental-health-conditions/anxiety/#Treatinganxiety\">Treating Anxiety</a>"
const val anxiety2 = "<a href=\"https://www.nhs.uk/mental-health/conditions/generalised-anxiety-disorder/self-help/\">Self Help</a>"
const val anxiety3 = "<a href=\"https://www.mind.org.uk/information-support/types-of-mental-health-problems/anxiety-and-panic-attacks/self-care/\">Self Care</a>"

const val depression1 = "<a href=\"https://www.mind.org.uk/information-support/types-of-mental-health-problems/depression/self-care/\">Self Care</a>"
const val depression2 = "<a href=\"https://www.youngminds.org.uk/young-person/mental-health-conditions/depression/\">Depression</a>"
const val depression3 = "<a href=\"https://www.nhs.uk/mental-health/feelings-symptoms-behaviours/feelings-and-symptoms/low-mood-sadness-depression/\">Low Mood Sadness Depression</a>"

class SurveyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySurveyBinding
    private lateinit var root: View
    private lateinit var surveyPage1 : SurveyPage
    private lateinit var surveyPage2 : SurveyPage
    private lateinit var surveyPage3 : SurveyPage
    private lateinit var recyclerView : RecyclerView
    private var pageNum = 1
    private val pagesCount = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        binding.surveyToolbar.setSubtitleTextColor(Color.WHITE)
        root = binding.root

        cancelNotification()

        setContentView(root)
        serve()
    }

    private fun cancelNotification() {
        with(NotificationManagerCompat.from(applicationContext)) {
           try {
               val id = 13132
               cancel(id)
           }catch (e:Exception){}
        }
    }

    private fun serve(){
        binding.surveyToolbar.subtitle = "Survey $pageNum of $pagesCount"
        when (pageNum) {
            1 -> binding.submitSurvey.text = getString(R.string.next)
            2 -> binding.submitSurvey.text = getString(R.string.next)
            else -> binding.submitSurvey.text = getString(R.string.submit_form)
        }
        when(pageNum){
            1 -> serveSurvey1()
            2 -> serveSurvey2()
            3 -> serveSurvey3()
        }
    }
    private fun serveSurvey1(){
        surveyPage1 = SurveyPage(pageNum, PrepareQuestions.getSurveyPage1(applicationContext))
        recyclerView = binding.surveyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = QuestionsAdapter(surveyPage1, applicationContext)

        binding.submitSurvey.setOnClickListener {
            surveyPage1.surveyItems.forEach {
                if (it is SurveyQuestion){
                    if (it.pickedAnswerValue == null) {
                        showInCompleteAnswersWarning(this)
                        return@setOnClickListener
                    }
                    it.pickedAnswerValue = it.answers[it.answerText]
                }
            }
            pageNum++
            serve()
            binding.scrollable.scrollTo(0,0)

        }
    }
    private fun serveSurvey2() {
        surveyPage2 = SurveyPage(pageNum, PrepareQuestions.getSurveyPage2(applicationContext))
        recyclerView = binding.surveyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = QuestionsAdapter(surveyPage2, applicationContext)

        binding.submitSurvey.setOnClickListener {
            surveyPage2.surveyItems.forEach { surveyItem ->
                if (surveyItem is SurveyQuestion){
                    if(surveyItem.pickedAnswerValue == null){
                        showInCompleteAnswersWarning(this)
                        return@setOnClickListener
                    }
//                    surveyItem.answerText =
//                        surveyItem.answers[surveyItem.pickedAnswerValue!!]
                    surveyItem.pickedAnswerValue =
                        surveyItem.answers[surveyItem.answerText]
                }
            }
            pageNum++
            serve()
            binding.scrollable.scrollTo(0,0)
        }
    }

    private fun serveSurvey3() {
        surveyPage3 = SurveyPage(pageNum, PrepareQuestions.getSurveyPage3(applicationContext))
        recyclerView = binding.surveyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = QuestionsAdapter(surveyPage3, applicationContext)

        binding.submitSurvey.setOnClickListener {
            surveyPage3.surveyItems.forEach {
                if(it is SurveyQuestion && it.pickedAnswerValue == null){
                    it.pickedAnswerValue = 0
                }
            }
            saveAnswers()
            checkScore()
        }
    }

    private fun checkScore() {
        val scoreGad = surveyPage1.getGadScore()
        val phqScore = surveyPage1.getPhqScore()

        var message1 = ""
        var message2 = ""
        val layout = LinearLayout(this).also {
            it.orientation = LinearLayout.VERTICAL
            it.setPadding(30)
         }
        if (scoreGad!=null && scoreGad>9) {
            message1 += "we noticed your anxiety score is high please see Student's health GP\n" + "or you can visit the following sites"
            layout.addView(TextView(layout.context).also {
                it.text = message1
                it.textSize = 16.0F
            })
            layout.addView(TextView(layout.context).also {
                it.text = HtmlCompat.fromHtml(anxiety1, HtmlCompat.FROM_HTML_MODE_LEGACY)
                it.textSize = 16.0F
                it.movementMethod = LinkMovementMethod.getInstance()
            })
            layout.addView(TextView(layout.context).also {
                it.text = HtmlCompat.fromHtml(anxiety2, HtmlCompat.FROM_HTML_MODE_LEGACY)
                it.textSize = 16.0F
                it.movementMethod = LinkMovementMethod.getInstance()
            })
            layout.addView(TextView(layout.context).also {
                it.text = HtmlCompat.fromHtml(anxiety3, HtmlCompat.FROM_HTML_MODE_LEGACY)
                it.textSize = 16.0F
                it.movementMethod = LinkMovementMethod.getInstance()
            })
        }

        if (phqScore!=null && phqScore>9) {
            message2 += "we noticed your depression score is high please see Student's health GP\n" + "or you can visit the following sites"
            layout.addView(TextView(layout.context).also {
                it.text = message2
                it.textSize = 16.0F
            })
            layout.addView(TextView(layout.context).also {
                it.text = HtmlCompat.fromHtml(depression1, HtmlCompat.FROM_HTML_MODE_LEGACY)
                it.textSize = 16.0F
                it.movementMethod = LinkMovementMethod.getInstance()
            })
            layout.addView(TextView(layout.context).also {
                it.text = HtmlCompat.fromHtml(depression2, HtmlCompat.FROM_HTML_MODE_LEGACY)
                it.textSize = 16.0F
                it.movementMethod = LinkMovementMethod.getInstance()
            })
            layout.addView(TextView(layout.context).also {
                it.text = HtmlCompat.fromHtml(depression3, HtmlCompat.FROM_HTML_MODE_LEGACY)
                it.textSize = 16.0F
                it.movementMethod = LinkMovementMethod.getInstance()
            })
        }

        if (message1.isNotEmpty() or message2.isNotEmpty()){
            AlertDialog.Builder(this)
                .setView(layout)
                .setTitle("Score notification")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { d, _ ->
                    d.dismiss()
                }
                .setOnDismissListener{
                    dialogDismissed = true
                    goHomeActivity()
                }
                .create()
                .show()
        }else {
            goHomeActivity()
            dialogDismissed = true
        }
    }

    private var dateUpdated = false
    private var dialogDismissed = false

    private fun goHomeActivity(){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun saveAnswers() {
        val onFinish = {
            dateUpdated = true
            goHomeActivity()
        }
        Amplify.Auth?.currentUser?.userId?.let {
            val message = "Saving answers"
            SurveysViewModel()
                .saveSurveyData(it, surveyPage1, surveyPage2, surveyPage3, onFinish)

            Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
        }
    }

    private fun showInCompleteAnswersWarning(context: Context){
        AlertDialog.Builder(context)
            .setTitle(R.string.incomplete_answers)
            .setPositiveButton(android.R.string.ok) { d , int ->
                d.dismiss()
            }
            .create()
            .show()
    }
}