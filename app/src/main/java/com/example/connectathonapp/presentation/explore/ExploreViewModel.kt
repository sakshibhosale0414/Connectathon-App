package com.example.connectathonapp.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectathonapp.data.ExploreUiState
import com.example.connectathonapp.data.People
import com.example.connectathonapp.repository.PeopleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: PeopleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState(isLoading = true))
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private val _connectionMessage = MutableSharedFlow<String>()
    val connectionMessage = _connectionMessage.asSharedFlow()

    private val _selectedDomain = MutableStateFlow<String?>(null)
    val selectedDomain: StateFlow<String?> = _selectedDomain.asStateFlow()

    private val _selectedInterest = MutableStateFlow<String?>(null)
    val selectedInterest: StateFlow<String?> = _selectedInterest.asStateFlow()

    private var allPeople: List<People> = emptyList()

    init {
        fetchPeople()
    }

    fun fetchPeople() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                allPeople = repository.fetchPeople()
                android.util.Log.d("ExploreViewModel", "Fetched ${allPeople.size} people")
                allPeople.forEachIndexed { index, person ->
                    android.util.Log.d("ExploreViewModel", "Person $index: ${person.name}, age=${person.age}, domain=${person.domain}, image=${person.image.take(50)}...")
                }
                applyFiltersToLoadedCards()
            } catch (e: Exception) {
                android.util.Log.e("ExploreViewModel", "Error fetching people", e)
                _uiState.value = ExploreUiState(error = e.message)
            }
        }
    }

    fun applyFilter(domain: String?, interest: String?) {
        _selectedDomain.value = domain
        _selectedInterest.value = interest
        applyFiltersToLoadedCards()
    }

    private fun applyFiltersToLoadedCards() {
        val filtered = allPeople.filter { person ->
            val domainMatch = _selectedDomain.value?.let { it == person.domain } ?: true
            val interestMatch = _selectedInterest.value?.let { person.interests.contains(it) } ?: true
            domainMatch && interestMatch
        }
        android.util.Log.d("ExploreViewModel", "Filtered to ${filtered.size} people")
        _uiState.value = _uiState.value.copy(people = filtered, isLoading = false)
    }

    fun connect(person: People) {
        viewModelScope.launch {
            _connectionMessage.emit("✨ Connected with ${person.name}!")
        }
    }
}
