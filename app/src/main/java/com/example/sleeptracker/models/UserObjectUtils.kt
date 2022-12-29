package com.example.sleeptracker.models

import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.User
import com.example.sleeptracker.aws.AWS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import com.amplifyframework.kotlin.core.Amplify as AmplifyKt


fun getNonNullUserValue(onComplete: (user: User) -> Unit) {
    AWS.uid{
        val id = it ?: return@uid
        Amplify.DataStore.query(
            User::class.java, Where.identifier(User::class.java,id),
            { users ->
                val u = try {
                    users.next()
                }catch (_:Exception){null}
                if (u!=null){
                    onComplete(u)
                }else {
                    Amplify.DataStore.observe(
                        User::class.java, id,
                        {}, { user->
                            onComplete(user.item())
                        },{},{})
                }
            },
            {}
        )
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun  getUserValueSuspendable() : User?{
    val id = AmplifyKt.Auth.getCurrentUser().userId
    var u : User? = null
    AmplifyKt.DataStore.query(User::class, Where.identifier(User::class.java,id))
        .catch {

        }
        .collect {
           u =  it
        }
    return  u
}

