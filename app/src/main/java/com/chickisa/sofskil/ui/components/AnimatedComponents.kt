package com.chickisa.sofskil.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.chickisa.sofskil.ui.theme.PrimaryGreen
import kotlinx.coroutines.delay

@Composable
fun BubbleAnimation(
    show: Boolean,
    onDismiss: () -> Unit = {}
) {
    if (show) {
        LaunchedEffect(Unit) {
            delay(2000)
            onDismiss()
        }
        
        val infiniteTransition = rememberInfiniteTransition(label = "bubble")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
        
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
        
        Box(
            modifier = Modifier
                .size(60.dp)
                .scale(scale)
                .alpha(alpha)
                .clip(CircleShape)
                .background(PrimaryGreen.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text("🫧", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun CleaningAnimation(
    show: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = show,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "cleaning")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )
            
            Text(
                "🧹",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.graphicsLayer {
                    rotationZ = rotation
                }
            )
        }
    }
}

@Composable
fun SuccessAnimation(
    show: Boolean,
    text: String = "Success!",
    emoji: String = "✨"
) {
    AnimatedVisibility(
        visible = show,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(PrimaryGreen)
                .padding(16.dp)
        ) {
            Text(emoji, style = MaterialTheme.typography.titleLarge)
            Text(
                text,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

