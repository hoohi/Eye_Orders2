package com.eyeorders.data.remote.firebase

import com.eyeorders.data.model.order.neworder.NewOrderItem
import com.eyeorders.data.prefs.PrefsDataManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject


class FirebaseSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val prefsDataManager: PrefsDataManager,
    private val gson: Gson,
) {


    fun getNewOrders(): Flow<List<NewOrderItem>> = callbackFlow {
        val user = prefsDataManager.getUser()
        val reference = firebaseDatabase.getReference(VENDOR_KEY.plus(user.id)).orderByKey()

        val listener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                cancel(error.message, error.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    val jsonString = gson.toJson(snapshot.value)
                    Timber.d("Firebase: $jsonString")
                    val orderResponse = gson.fromJson<HashMap<String, NewOrderItem>>(
                        jsonString,
                        object : TypeToken<HashMap<String, NewOrderItem>>() {}.type
                    )
                    Timber.d("Firebase: $orderResponse")
                    trySend(orderResponse.values.toList())
                } else {
                    trySend(emptyList<NewOrderItem>())
                }
            }
        }

        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    companion object {
        private const val VENDOR_KEY = "vendor_"
    }

}