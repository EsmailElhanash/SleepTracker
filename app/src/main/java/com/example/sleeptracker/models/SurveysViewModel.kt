package com.example.sleeptracker.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.SurveyEntry
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.aws.DB
import com.example.sleeptracker.ui.survey.utils.*
import com.example.sleeptracker.utils.time.DAY_IN_MS
import org.json.JSONObject
import java.util.*

class SurveysViewModel : ViewModel() {
    var surveyLastUpdated: MutableLiveData<Long> = MutableLiveData()
    var surveyLastShownCase2: MutableLiveData<Long> = MutableLiveData()
    init {
        Amplify.Auth.currentUser.userId
            .let {
                loadSurveyLastUpdatedDate(it)
            }
    }

    private fun loadSurveyLastUpdatedDate(uid:String,onCompleteCallback: ((lastUpdatedCase1:Long) -> Unit)? = null) {
        DB.get(uid,User::class.java){ response ->
            val u = (response.data as User)
            surveyLastUpdated.postValue(u.surveyLastUpdate?.toDate()?.time ?: 0)
            surveyLastShownCase2.postValue(u.surveyLastUpdate2?.toDate()?.time ?: 0)
            surveyLastUpdated.value?.let {
                if (onCompleteCallback != null) {
                    onCompleteCallback(it)
                }
            }
        }
    }

    fun saveSurveyData(uid: String, surveyPage: SurveyPage, surveyPage2: SurveyPage, surveyPage3: SurveyPage,onCompleteCallback: (() -> Unit)) {
        val survey1 = JSONObject()
        survey1.put("phq9Score" , surveyPage.getPhqScore())
        survey1.put("gad7Score" , surveyPage.getGadScore())


        val survey2 = JSONObject()
//        val surveyMap : ArrayList<Map<String,String>> = getSurvey2Map(surveyPage2)
        for (i in surveyPage2.surveyItems){
            if (i is SurveyQuestion){
                i.sentence?.let {
                    val answer = JSONObject()
                    answer.put("Answer" , i.answerText)
                    answer.put("Score" , i.pickedAnswerValue)
                    survey2.put(it,answer)
                }
            }
        }
        survey2.put("Total Score",surveyPage2.getTotalScore())

        val survey3 = JSONObject()
        val data3 = getSurvey3Answers(surveyPage3)
        data3.forEach{
            survey3.put(it.key,it.value)
        }
        val nowMS = Calendar.getInstance().timeInMillis
        val time = Temporal.DateTime(Date(nowMS),0)
        val surveyEntry = SurveyEntry.builder()
            .date(time)
            .survey1(survey1.toString())
            .survey2(survey2.toString())
            .survey3(survey3.toString())
            .id(time.toString())
            .userSurveysId("Survey:$time-User:$uid")
            .build()
        DB.save(surveyEntry){
        }
        saveDate(uid,onCompleteCallback)
    }

    private fun saveDate(uid: String,onCompleteCallback: (() -> Unit)) {
        val nowMS = Calendar.getInstance().timeInMillis
        val lastUpdate = surveyLastUpdated.value
        val save = { last : Long ->
            if (nowMS>=(last+28* DAY_IN_MS)) {
                surveyLastUpdated.postValue(nowMS)
                DB.get(uid,User::class.java){
                    val u = (it.data as User)
                        .copyOfBuilder()
                        .surveyLastUpdate(Temporal.DateTime(Date(nowMS),0)).build()
                    DB.save(u){
                        onCompleteCallback()
                    }
                }

            }
        }
        if (lastUpdate == null) {
            loadSurveyLastUpdatedDate(uid){
                save(it)
            }
        }else {
            save(lastUpdate)
        }

    }
}
