package com.example.connectathonapp.presentation.explore

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit,
    cardKey: Any = Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val offsetX = remember(cardKey) { Animatable(0f) }
    val rotation = remember(cardKey) { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val threshold = 200f
    val maxRotation = 15f

    // Reset position when card changes
    LaunchedEffect(cardKey) {
        offsetX.snapTo(0f)
        rotation.snapTo(0f)
    }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.value.toInt(), 0) }
            .graphicsLayer {
                rotationZ = rotation.value
                alpha = 1f - kotlin.math.abs(offsetX.value) / 1000f
            }
            .pointerInput(cardKey) {
                detectDragGestures(
                    onDrag = { _, dragAmount ->
                        scope.launch {
                            val newOffset = offsetX.value + dragAmount.x
                            offsetX.snapTo(newOffset)
                            rotation.snapTo((newOffset / 400f) * maxRotation)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > threshold -> { 
                                    offsetX.animateTo(2000f, spring())
                                    rotation.animateTo(maxRotation, spring())
                                    // Small delay to ensure animation completes
                                    delay(100)
                                    onSwipedRight()
                                }
                                offsetX.value < -threshold -> { 
                                    offsetX.animateTo(-2000f, spring())
                                    rotation.animateTo(-maxRotation, spring())
                                    // Small delay to ensure animation completes
                                    delay(100)
                                    onSwipedLeft()
                                }
                                else -> { 
                                    offsetX.animateTo(0f, spring())
                                    rotation.animateTo(0f, spring())
                                }
                            }
                        }
                    }
                )
            }
    ) { content() }
}
