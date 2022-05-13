package com.example.sleeptracker.ui.survey.utils



data class SurveyPage(val ID: Int, var surveyItems : ArrayList<SurveyItem> ){

        fun getTotalScore() : Int {
                var totalScore = 0
                surveyItems.forEach {
                        if (it is SurveyQuestion)
                                if (it.pickedAnswerValue!=null)
                                        totalScore+=it.pickedAnswerValue!!
                }
                return totalScore
        }

        fun getGadScore() : Int?{
                if (ID == 1){
                        var scoreGad = 0
                        surveyItems.forEach {
                                if (it is SurveyQuestion){
                                        if (it.groupName == GAD_GROUP_NAME){
                                                scoreGad+= if (it.pickedAnswerValue!=null) it.pickedAnswerValue!! else 0
                                        }
                                }
                        }
                        return scoreGad
                }
                return  null
        }

        fun getPhqScore() : Int?{
                if (ID == 1){
                        var scorePhq = 0
                        surveyItems.forEach {
                                if (it is SurveyQuestion){
                                        if (it.groupName == PHQ_GROUP_NAME){
                                                scorePhq+= if (it.pickedAnswerValue!=null) it.pickedAnswerValue!! else 0
                                        }
                                }
                        }
                        return scorePhq
                }
                return  null
        }
}