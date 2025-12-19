package com.example.connectathonapp.presentation.explore

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDomain by viewModel.selectedDomain.collectAsState()
    val selectedInterest by viewModel.selectedInterest.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.connectionMessage.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        // Filter Section
        FilterSection(
            selectedDomain = selectedDomain,
            onDomainSelected = { viewModel.applyFilter(it, selectedInterest) },
            selectedInterest = selectedInterest,
            onInterestSelected = { viewModel.applyFilter(selectedDomain, it) },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Card Stack
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.people.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No profiles found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    TinderCardStack(
                        people = uiState.people,
                        onConnect = { person ->
                            scope.launch {
                                viewModel.connect(person)
                            }
                        },
                        onSkip = { person ->
                            // Skip action - can be extended later
                        }
                    )
                }
            }
        }
    }
}
