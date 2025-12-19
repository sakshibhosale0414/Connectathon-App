package com.example.connectathonapp.repository

import android.util.Log
import com.example.connectathonapp.data.People
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class PeopleRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun fetchPeople(): List<People> =
        suspendCancellableCoroutine { cont ->
            firestore.collection("people")
                .get()
                .addOnSuccessListener { result ->

                    // ✅ DEBUG LOGS
                    Log.d("Firestore", "fetchPeople → Docs fetched: ${result.size()}")
                    val list = result.toObjects(People::class.java)
                    Log.d("Firestore", "fetchPeople → Mapped objects: ${list.size}")

                    cont.resume(list)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "fetchPeople → Error", exception)
                    cont.resumeWithException(exception)
                }
        }

    suspend fun fetchFilteredPeople(
        domain: String?,
        interest: String?
    ): List<People> =
        suspendCancellableCoroutine { cont ->

            var query: Query = firestore.collection("people")

            domain?.let {
                query = query.whereEqualTo("domain", it)
            }

            interest?.let {
                query = query.whereArrayContains("interests", it)
            }

            query.get()
                .addOnSuccessListener { result ->

                    // ✅ DEBUG LOGS
                    Log.d("Firestore", "fetchFilteredPeople → Docs fetched: ${result.size()}")
                    val list = result.toObjects(People::class.java)
                    Log.d("Firestore", "fetchFilteredPeople → Mapped objects: ${list.size}")

                    cont.resume(list)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "fetchFilteredPeople → Error", exception)
                    cont.resumeWithException(exception)
                }
        }
}
