package com.example.connectathonapp.presentation.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun FilterSection(
    selectedDomain: String?,
    onDomainSelected: (String?) -> Unit,
    selectedInterest: String?,
    onInterestSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val domains = listOf("Tech", "Design", "Business", "Marketing")
    val interests = listOf("Coding", "Photography", "UI/UX", "Startups", "Branding")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilterGroup("Domain", domains, selectedDomain, onDomainSelected)
        FilterGroup("Interests", interests, selectedInterest, onInterestSelected)
    }
}

@Composable
private fun FilterGroup(
    title: String,
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (selectedItem != null) TextButton(onClick = { onItemSelected(null) }) { Text("Clear") }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items.size) { index ->
                val item = items[index]
                FilterChip(
                    selected = selectedItem == item,
                    onClick = { onItemSelected(if (selectedItem == item) null else item) },
                    label = { Text(item) }
                )
            }
        }
    }
}
