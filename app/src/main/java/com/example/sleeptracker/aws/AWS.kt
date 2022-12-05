package com.example.sleeptracker.aws

import android.util.Log
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.QuerySortBy
import com.amplifyframework.core.model.query.QuerySortOrder
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.DataStoreChannelEventName
import com.amplifyframework.datastore.appsync.ModelWithMetadata
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.hub.HubChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AWS {
    private const val TAG = "DBOBJECT"
    data class Response (val success:Boolean, val data:Model?, val error:String?)
    data class PredicateResponse (val success:Boolean, val data:List<Model>?, val error:String?)


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

    fun uid () =
        Amplify.Auth?.currentUser?.userId


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
        Amplify.Hub.subscribe(
            HubChannel.DATASTORE,
            { it.name == DataStoreChannelEventName.OUTBOX_MUTATION_FAILED.toString() },
            {
                Log.i("MyAmplifyApp", "User has a network connection? ")
                amplifyRetry()
            }
        )
        Amplify.Hub.subscribe(
            HubChannel.DATASTORE,
            { it.name == DataStoreChannelEventName.SUBSCRIPTION_DATA_PROCESSED.toString() &&
                    (it.data as ModelWithMetadata<*> ).model is User
            },
            {
                Log.i("MyAmplifyApp", "User has a network connection? ")
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