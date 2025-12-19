package com.example.connectathonapp.presentation.explore

import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.connectathonapp.data.People

@Composable
fun TinderCardStack(
    people: List<People>,
    onConnect: (People) -> Unit,
    onSkip: (People) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentIndex by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(people) { 
        if (currentIndex >= people.size) {
            currentIndex = 0
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            people.isEmpty() -> {
                EmptyState(message = "No profiles available")
            }

            currentIndex >= people.size -> {
                EndState(
                    onRefresh = { currentIndex = 0 }
                )
            }

            else -> {
                // Card Stack Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.75f),
                    contentAlignment = Alignment.Center
                ) {
                    // Background cards (next 2 cards for depth effect)
                    people.getOrNull(currentIndex + 2)?.let { person ->
                        BackgroundCard(
                            person = person,
                            offset = 20.dp,
                            scale = 0.90f,
                            zIndex = 0f
                        )
                    }

                    people.getOrNull(currentIndex + 1)?.let { person ->
                        BackgroundCard(
                            person = person,
                            offset = 12.dp,
                            scale = 0.94f,
                            zIndex = 1f
                        )
                    }

                    // Main swipeable card
                    val person = people[currentIndex]

                    SwipeableCard(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(2f),
                        onSwipedLeft = {
                            onSkip(person)
                            if (currentIndex < people.size - 1) currentIndex++
                        },
                        onSwipedRight = {
                            onConnect(person)
                            if (currentIndex < people.size - 1) currentIndex++
                        }
                    ) {
                        ProfileCard(
                            person = person,
                            onConnect = {
                                onConnect(person)
                                if (currentIndex < people.size - 1) currentIndex++
                            },
                            onSkip = {
                                onSkip(person)
                                if (currentIndex < people.size - 1) currentIndex++
                            }
                        )
                    }
                }

                // Action Buttons
                SwipeActionButtons(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                    onSkip = {
                        onSkip(people[currentIndex])
                        if (currentIndex < people.size - 1) currentIndex++
                    },
                    onConnect = {
                        onConnect(people[currentIndex])
                        if (currentIndex < people.size - 1) currentIndex++
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileCard(
    person: People,
    onConnect: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageUrl = person.image.ifEmpty { "https://via.placeholder.com/400" }

    Card(
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Profile Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "${person.name}'s profile picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Profile Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name and Age
                Text(
                    text = "${person.name}, ${person.age}",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )

                // Domain badge
                if (person.domain.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = person.domain,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Bio
                Text(
                    text = person.bio,
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 16.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 22.sp
                )

                // Interests
                if (person.interests.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        person.interests.take(3).forEach { interest ->
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = interest,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BackgroundCard(
    person: People,
    offset: Dp,
    scale: Float,
    zIndex: Float,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(top = offset)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .zIndex(zIndex)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(person.image.ifEmpty { "https://via.placeholder.com/400" })
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(8.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.5f
            )
        }
    }
}

@Composable
fun SwipeActionButtons(
    onSkip: () -> Unit,
    onConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Skip Button
        Button(
            onClick = onSkip,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(140.dp)
                .height(56.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "Skip",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Connect Button
        Button(
            onClick = onConnect,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(140.dp)
                .height(56.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = "Connect",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EndState(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            fontSize = 72.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You've seen everyone!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Check back later for new profiles",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRefresh,
            modifier = Modifier.height(48.dp)
        ) {
            Text("Start Over")
        }
    }
}
