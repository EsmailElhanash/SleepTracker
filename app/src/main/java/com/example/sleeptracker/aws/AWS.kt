package com.example.sleeptracker.aws

import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.api.aws.AuthModeStrategyType
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.QuerySortBy
import com.amplifyframework.core.model.query.QuerySortOrder
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.*
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.kotlin.datastore.DataStore
import com.example.sleeptracker.App
import com.example.sleeptracker.R
import com.example.sleeptracker.background.androidservices.TrackerService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object AWS {
    private const val TAG = "DBOBJECT"
    data class Response (val success:Boolean, val data:Model?, val error:String?)
    data class PredicateResponse (val success:Boolean, val data:List<Model>?, val error:String?)


    fun get(id:String, c: Class<out Model>, onComplete:(res:Response) -> Unit) {
        Log.d(TAG, "get:")
        Amplify.DataStore.query(
            c, Where.id(id),
            {
                if (it.hasNext())
                    onComplete(Response(true,it.next(),null))
                else
                    onComplete(Response(false,null,null))
            },
            {
                Log.d(TAG, "get: $it")
                it.localizedMessage?.let { it1 -> onComplete(Response(false,null,it1)) }
            }
        )
    }


    fun getPredicate(predicate:QueryPredicate, c: Class<out Model>, onComplete:(res:PredicateResponse) -> Unit){
        Amplify.DataStore.query(
            c, Where.matchesAndSorts(predicate, listOf(QuerySortBy("createdAt",QuerySortOrder.DESCENDING))),
            {
                onComplete(PredicateResponse(true,it.asSequence().toList(),null))
            },
            {
                it.localizedMessage?.let { it1 -> onComplete(PredicateResponse(false,null,it1)) }
            }
        )
    }


    fun save(model: Model, onComplete:(res:Response) -> Unit){
        Amplify.DataStore.save(model,
            {
                onComplete(Response(true,it.item(),null))
            },
            {
                it.localizedMessage?.let { it1 -> onComplete(Response(false,null,it1)) }
                amplifyRetry()
            }
        )

    }

    val uid by lazy {
        Amplify.Auth?.currentUser?.userId
    }

    fun amplifyRetry(){
        Amplify.DataStore.stop({
            CoroutineScope(Dispatchers.IO).launch {
                delay(5000)
                Amplify.DataStore.start({},{})
        }},{})
    }

    fun hub(){
        Amplify.Hub.subscribe(
            HubChannel.DATASTORE,
            { it.name == DataStoreChannelEventName.OUTBOX_MUTATION_FAILED.toString() },
            {
                Log.i("MyAmplifyApp", "User has a network connection? ")
                amplifyRetry()
            }
        )
        Amplify.Hub.subscribe(HubChannel.AUTH,
            {
                // Listen for sign out events.
                it.name.equals(AuthChannelEventName.SIGNED_OUT.toString())
            },
            {
                // When one arrives, clear the DataStore.
                Amplify.DataStore.clear(
                    { Log.i("MyAmplifyApp", "DataStore is cleared") },
                    { Log.e("MyAmplifyApp", "Failed to clear DataStore") }
                )
            }
        )
    }
}