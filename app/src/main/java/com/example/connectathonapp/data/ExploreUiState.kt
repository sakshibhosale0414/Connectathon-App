package com.example.connectathonapp.data

data class ExploreUiState(
    val people: List<People> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
