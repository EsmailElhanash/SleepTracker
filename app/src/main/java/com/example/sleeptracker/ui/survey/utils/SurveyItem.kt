package com.example.sleeptracker.ui.survey.utils

abstract class SurveyItem(
        //Survey might have groups of questions on it
        open val groupName: String?,
        open val surveyNumber: Int,
        open val sentence: String?,
)

class SurveyQuestion(
        override val groupName: String?,
        override val surveyNumber: Int,
        override val sentence: String?,
        var pickedAnswerValue: Int? = null,
        var answerText: String? = null,
        var answers: Map<String,Int>
) : SurveyItem(groupName,surveyNumber,sentence)

class SurveyTitle(
        override val groupName: String?,
        override val surveyNumber: Int,
        override val sentence: String?,
) : SurveyItem(groupName,surveyNumber,sentence)