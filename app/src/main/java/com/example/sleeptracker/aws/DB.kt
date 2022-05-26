package com.example.sleeptracker.aws

import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.QuerySortBy
import com.amplifyframework.core.model.query.QuerySortOrder
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.kotlin.datastore.DataStore

object DB {

    data class Response (val success:Boolean, val data:Model?, val error:String?)
    data class PredicateResponse (val success:Boolean, val data:List<Model>?, val error:String?)

    @Synchronized
    fun get(id:String, c: Class<out Model>, onComplete:(res:Response) -> Unit){
        Amplify.DataStore.query(
            c, Where.id(id),
            {
                onComplete(Response(true,it.next(),null))
            },
            {
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