package com.example.connectathonapp.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedConnectionLogo()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Connectathon",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Where interest's spark connections.",
                fontSize = 16.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
fun AnimatedConnectionLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "connection")

    val lineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lineProgress"
    )

    val dotScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotScale"
    )

    Canvas(modifier = Modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 3

        val nodes = listOf(
            Offset(center.x, center.y - radius),
            Offset(center.x + radius * 0.95f, center.y - radius * 0.31f),
            Offset(center.x + radius * 0.59f, center.y + radius * 0.81f),
            Offset(center.x - radius * 0.59f, center.y + radius * 0.81f),
            Offset(center.x - radius * 0.95f, center.y - radius * 0.31f)
        )

        val primaryColor = Color(0xFF2196F3)
        val secondaryColor = Color(0xFF64B5F6)

        nodes.forEachIndexed { i, start ->
            val end = nodes[(i + 2) % nodes.size]

            val animatedEnd = Offset(
                start.x + (end.x - start.x) * lineProgress,
                start.y + (end.y - start.y) * lineProgress
            )

            drawLine(
                color = primaryColor.copy(alpha = 0.6f),
                start = start,
                end = animatedEnd,
                strokeWidth = 4.dp.toPx()
            )
        }

        // Draw connection nodes (dots)
        nodes.forEach { offset ->
            drawCircle(
                color = primaryColor,
                radius = 12.dp.toPx() * dotScale,
                center = offset
            )
            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx() * dotScale,
                center = offset
            )
            drawCircle(
                color = secondaryColor,
                radius = 5.dp.toPx() * dotScale,
                center = offset
            )
        }

        drawCircle(
            color = primaryColor.copy(alpha = 0.2f),
            radius = 30.dp.toPx() * dotScale,
            center = center
        )
        drawCircle(
            color = primaryColor,
            radius = 20.dp.toPx(),
            center = center
        )
    }
}