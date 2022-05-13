package com.example.sleeptracker.ui.survey

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sleeptracker.R
import com.example.sleeptracker.ui.MainActivity
import com.example.sleeptracker.ui.survey.utils.SurveyPage
import com.example.sleeptracker.ui.survey.utils.SurveyQuestion
import com.google.android.material.slider.Slider
import kotlin.random.Random


class QuestionsAdapter(private val surveyPage: SurveyPage, private val appCtx: Context
) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    private var answerType = AnswerType.ANSWER_PICKER

    init {
        if (MainActivity.TEST) fillAnswersForTesting()
        if (surveyPage.ID == 3)
            answerType = AnswerType.SEEK_BAR
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_view, parent, false)
        return ViewHolder(view)
    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = surveyPage.surveyItems[holder.bindingAdapterPosition]
        holder.questionSentence.text = item.sentence

        if (item is SurveyQuestion){
            if (answerType == AnswerType.SEEK_BAR){
                holder.answerBar.visibility = View.VISIBLE
                holder.answerBar.value = item.pickedAnswerValue?.toFloat() ?: 0f
                holder.answerBar.addOnChangeListener{ _, value, _ ->
                    item.pickedAnswerValue = value.toInt()
                    item.answerText = value.toString()
                }
            }else if (answerType == AnswerType.ANSWER_PICKER){
                holder.answerPicker.visibility = View.VISIBLE
                holder.answerPicker.adapter =
                    AnswersSpinnerAdapter(appCtx, getAnswersWithPrompt(ArrayList(item.answers.keys)))
                item.pickedAnswerValue?.let { holder.answerPicker.setSelection(it) }

                holder.answerPicker.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                        if (pos == 0) {
                            item.pickedAnswerValue = null
                            return
                        }
                        item.pickedAnswerValue = pos
                        item.answerText = item.answers.keys.elementAt(pos-1)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?){}
                }
            }
        }
    }

    private fun fillAnswersForTesting(){
        if (surveyPage.ID != 3)
            surveyPage.surveyItems.forEachIndexed { index, item ->
                if (item is SurveyQuestion) {
                    val answersSize = item.answers.size
                    answersSize.let {
                        (surveyPage.surveyItems[index] as SurveyQuestion)
                            .pickedAnswerValue = Random.nextInt(1, answersSize)
                    }

                }
            }
    }

    private fun getAnswersWithPrompt(items : ArrayList<String>) : ArrayList<String>{
        if (items.isNotEmpty())
            if (items[0]!=appCtx.getString(R.string.choose_answer))
                items.add(0,appCtx.getString(R.string.choose_answer))
        return items
    }

    override fun getItemCount(): Int = surveyPage.surveyItems.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var questionSentence: TextView = view.findViewById(R.id.questionSentence)
        var answerPicker: Spinner = view.findViewById(R.id.answerPicker)
        var answerBar: Slider = view.findViewById(R.id.answerSeekBar)
    }

    enum class AnswerType{
        SEEK_BAR,ANSWER_PICKER
    }
}