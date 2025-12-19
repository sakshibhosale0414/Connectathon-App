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

    private var currentDomain: String? = null
    private var currentInterest: String? = null

    init {
        fetchPeople()
    }

    fun fetchPeople() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val data = repository.fetchPeople()
                _uiState.value = ExploreUiState(people = data)
            } catch (e: Exception) {
                _uiState.value = ExploreUiState(error = e.message)
            }
        }
    }

    fun applyFilter(domain: String?, interest: String?) {
        currentDomain = domain
        currentInterest = interest

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val data = repository.fetchFilteredPeople(domain, interest)
                _uiState.value = ExploreUiState(people = data)
            } catch (e: Exception) {
                _uiState.value = ExploreUiState(error = e.message)
            }
        }
    }

    fun connect(person: People) {
        viewModelScope.launch {
            _connectionMessage.emit("✨ Connection built with ${person.name}!")
        }
    }
}
