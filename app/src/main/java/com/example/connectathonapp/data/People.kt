package com.example.connectathonapp.data

data class People(
    val name: String = "",
    val age: Int = 0,
    val bio: String = "",
    val domain: String = "",
    val interests: List<String> = emptyList(),
    val image: String = ""   // 🔥 MUST match Firestore field
)
