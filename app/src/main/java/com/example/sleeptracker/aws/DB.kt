package com.example.sleeptracker.aws

import android.util.Log
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.QuerySortBy
import com.amplifyframework.core.model.query.QuerySortOrder
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate

object DB {
    private const val TAG = "DBOBJECT"
    data class Response (val success:Boolean, val data:Model?, val error:String?)
    data class PredicateResponse (val success:Boolean, val data:List<Model>?, val error:String?)


    fun get(id:String, c: Class<out Model>, onComplete:(res:Response) -> Unit){
        Log.d(TAG, "get:")
        Amplify.DataStore.query(
            c, Where.id(id),
            {
                Log.d(TAG, "get: ${it.next()}")
                onComplete(Response(true,it.next(),null))
            },
            {
                Log.d(TAG, "get: $it")
                it.localizedMessage?.let { it1 -> onComplete(Response(false,null,it1)) }
                re()
            }
        )
    }

    private fun re(){
        Amplify.DataStore.start({},{})
    }

    @Synchronized
    fun getPredicate(predicate:QueryPredicate, c: Class<out Model>, onComplete:(res:PredicateResponse) -> Unit){
        Amplify.DataStore.query(
            c, Where.matchesAndSorts(predicate, listOf(QuerySortBy("createdAt",QuerySortOrder.DESCENDING))),
            {
                onComplete(PredicateResponse(true,it.asSequence().toList(),null))
            },
            {
                re()
                it.localizedMessage?.let { it1 -> onComplete(PredicateResponse(false,null,it1)) }
            }
        )
    }

    @Synchronized
    fun save(model: Model, onComplete:(res:Response) -> Unit){
        Amplify.DataStore.save(model,
            {
                    onComplete(Response(true,it.item(),null))
            },
            {
                re()
                it.localizedMessage?.let { it1 -> onComplete(Response(false,null,it1)) }
            }
        )

    }

    val uid  : String? = Amplify.Auth?.currentUser?.userId


}