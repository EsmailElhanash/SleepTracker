package com.example.sleeptracker.ui.survey.utils

import android.content.Context
import com.example.sleeptracker.R

object PrepareQuestions {
    fun getSurveyPage1(context:Context) : ArrayList<SurveyItem>{
        val id = 1
        val gad7Items:ArrayList<SurveyItem> = arrayListOf()
        val phq9Items:ArrayList<SurveyItem> = arrayListOf()

        val phq9qs = context.resources.getStringArray(R.array.phq9_questions)
        val gad7qs = context.resources.getStringArray(R.array.gad7_questions)

        //add title
        gad7Items.add(SurveyTitle(
                GAD_GROUP_NAME
                ,id
                ,context.getString(R.string.gad7_title)
        ))

        for (q in gad7qs){
            gad7Items.add(
                    SurveyQuestion(
                            GAD_GROUP_NAME
                            ,id
                            ,q
                            ,null,null
                            ,AnswersSurvey1
                    )
            )

        }


        //add title
        phq9Items.add(SurveyTitle(
                PHQ_GROUP_NAME
                ,id
                ,context.getString(R.string.phq9_title)
        ))

        for (q in phq9qs){
            phq9Items.add(
                    SurveyQuestion(
                            PHQ_GROUP_NAME
                            ,id
                            ,q
                            ,null,null
                            ,AnswersSurvey1
                    )
            )

        }
        val r = ArrayList<SurveyItem>()
        r.addAll(phq9Items)
        r.addAll(gad7Items)
        return r
    }

    fun getSurveyPage2(context:Context) : ArrayList<SurveyItem>{
        val id = 2
        val sci1:ArrayList<SurveyItem> = arrayListOf()
        val sci2:ArrayList<SurveyItem> = arrayListOf()
        val sci3:ArrayList<SurveyItem> = arrayListOf()

        val sci1qs = context.resources.getStringArray(R.array.sci_questions_1)
        val sci2qs = context.resources.getStringArray(R.array.sci_questions_2)
        val sci3qs = context.resources.getStringArray(R.array.sci_questions_3)

        var currentAddedQuestions = 0
        //add title
        sci1.add(SurveyTitle(
            SCI_GROUP_NAME
            ,id
            ,context.getString(R.string.sci_questions_1_title)
        ))

        //add questions
        for (q in sci1qs){
            sci1.add(
                SurveyQuestion(
                    SCI_GROUP_NAME
                    ,id
                    ,q
                    ,null,null
                    ,getSciAnswers(currentAddedQuestions)
                )
            )
            currentAddedQuestions++
        }

        //add title
        sci2.add(SurveyTitle(
            SCI_GROUP_NAME
            ,id
            ,context.getString(R.string.sci_questions_2_title)
        ))

        //add questions
        for (q in sci2qs){
            sci2.add(
                SurveyQuestion(
                    SCI_GROUP_NAME
                    ,id
                    ,q
                    ,null,null
                    ,getSciAnswers(currentAddedQuestions)
                )
            )
            currentAddedQuestions++

        }

        //add title
        sci3.add(SurveyTitle(
            SCI_GROUP_NAME
            ,id
            ,context.getString(R.string.sci_questions_3_title)
        ))

        //add questions
        for (q in sci3qs.withIndex()){
            sci3.add(
                SurveyQuestion(
                    SCI_GROUP_NAME
                    ,id
                    ,q.value
                    ,null,null
                    ,getSciAnswers(currentAddedQuestions)
                )
            )
            currentAddedQuestions++

        }
        val r = ArrayList<SurveyItem>()
        r.addAll(sci1)
        r.addAll(sci2)
        r.addAll(sci3)
        return r
    }


    fun getSurveyPage3(context:Context) : ArrayList<SurveyItem>{

        val id = 3
        val survey3:ArrayList<SurveyItem> = arrayListOf()

        val survey3qs = context.resources.getStringArray(R.array.survey3_questions)

        //add title
        survey3.add(SurveyTitle(
            "Survey3"
            ,id
            ,context.getString(R.string.survey3_title)
        ))

        //add questions
        for (q in survey3qs){
            survey3.add(
                SurveyQuestion(
                    "Survey3"
                    ,id
                    ,q
                    ,null,null
                    , getSurvey3Answers()
                )
            )

        }
        return survey3
    }

    private fun getSciAnswers(i: Int): Map<String,Int> {
        when(i){
            0,1 -> return AnswersSCI_1
            2 -> return AnswersSCI_2
            3 -> return AnswersSCI_3
            4,5,6 -> return AnswersSCI_4
            7 -> return AnswersSCI_5
        }
        return mapOf()
    }

    private fun getSurvey3Answers(): Map<String,Int> {

        val range = 0..7
        val strRange:HashMap<String,Int> = hashMapOf()
        range.forEach{
            strRange[it.toString()] = it
        }
        return strRange
    }
}