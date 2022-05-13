package com.example.sleeptracker.aws

import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthSession
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate

object DB {

    data class Response (val success:Boolean, val data:Model?, val error:String?)

    @Synchronized
    fun get(id:String, c: Class<out Model>, onComplete:(res:Response) -> Unit){
        Amplify.DataStore.query(
            c, Where.id(id),
            {
                onComplete(Response(true,it.next(),null))
            },
            {
                it.localizedMessage?.let { it1 -> onComplete(Response(false,null,it1)) }
            }
        )
    }

    @Synchronized
    fun getPredicate(predicate:QueryPredicate, c: Class<out Model>, onComplete:(res:Response) -> Unit){
        Amplify.DataStore.query(
            c, Where.matches(predicate),
            {
                onComplete(Response(true,it.next(),null))
            },
            {
                it.localizedMessage?.let { it1 -> onComplete(Response(false,null,it1)) }
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
            }
        )
    }

    val uid  : String? = Amplify.Auth?.currentUser?.userId


}