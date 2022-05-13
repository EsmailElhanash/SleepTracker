package com.example.sleeptracker.ui.survey.utils

fun getSurvey2Map (surveyPage2: SurveyPage) : ArrayList<Map<String,String>>
{
    val result : ArrayList<Map<String,String>> = arrayListOf()

    surveyPage2.surveyItems.forEachIndexed { _, it ->
        if (it is SurveyQuestion){
            if(it.answerText!=null&& it.answerText?.isNotEmpty() == true){

                it.sentence?.let { it2 ->
                    result.add(Survey2Answer(it2, it.answerText!!).getDBMap())
                }

            }else{
                it.sentence?.let {
                    result.add(Survey2Answer(it, "").getDBMap())
                }
            }
        }
    }
    return result
}

fun getSurvey3Answers(surveyPage3: SurveyPage) : HashMap<String,String>{
    val map = HashMap<String,String>()

    surveyPage3.surveyItems.forEach{
        if (it is SurveyQuestion) // 0 is survey title
            it.pickedAnswerValue?.let { it1 ->
                it.sentence?.let { it2 ->
                    map[it2] = it1.toString()
                }
            }
    }
    return map
}

data class Survey2Answer(var question : String, var answer : String){
    fun getDBMap() : Map<String,String>{
        return hashMapOf(question to answer)
    }
}