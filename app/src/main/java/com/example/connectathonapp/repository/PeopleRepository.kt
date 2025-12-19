package com.example.connectathonapp.repository

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
                    cont.resume(
                        result.toObjects(People::class.java)
                    )
                }
                .addOnFailureListener { cont.resumeWithException(it) }
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
                    cont.resume(
                        result.toObjects(People::class.java)
                    )
                }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
}
