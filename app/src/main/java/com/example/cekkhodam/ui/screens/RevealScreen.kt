package com.example.cekkhodam.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cekkhodam.theme.CosmicBgEnd
import com.example.cekkhodam.theme.CosmicBgStart
import com.example.cekkhodam.theme.CosmicGold
import com.example.cekkhodam.theme.PurpleSpark
import kotlinx.coroutines.delay

@Composable
fun RevealScreen(
    name: String,
    dob: String,
    onRevealFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loadingText by remember { mutableStateOf("Opening spiritual gateway...") }
    val rotationTransition = rememberInfiniteTransition(label = "CompassSpin")
    
    val angle by rotationTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "SpinAngle"
    )

    LaunchedEffect(Unit) {
        delay(600)
        loadingText = "Reading aura for $name..."
        delay(600)
        loadingText = "Calculating star alignment of $dob..."
        delay(600)
        loadingText = "Awakening companion..."
        delay(400)
        onRevealFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CosmicBgStart, CosmicBgEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Rotating Celestial Compass Logo
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .rotate(angle),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    
                    // Outer Ring
                    drawCircle(
                        color = CosmicGold,
                        radius = radius,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    
                    // Inner glowing circle
                    drawCircle(
                        color = PurpleSpark.copy(alpha = 0.2f),
                        radius = radius - 8.dp.toPx()
                    )

                    // Celestial lines
                    drawLine(
                        color = CosmicGold.copy(alpha = 0.5f),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = CosmicGold.copy(alpha = 0.5f),
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                    
                    // Small floating circles representing stars/nodes
                    drawCircle(
                        color = CosmicGold,
                        radius = 4.dp.toPx(),
                        center = Offset(size.width / 2, 16.dp.toPx())
                    )
                    drawCircle(
                        color = CosmicGold,
                        radius = 4.dp.toPx(),
                        center = Offset(size.width / 2, size.height - 16.dp.toPx())
                    )
                    drawCircle(
                        color = CosmicGold,
                        radius = 4.dp.toPx(),
                        center = Offset(16.dp.toPx(), size.height / 2)
                    )
                    drawCircle(
                        color = CosmicGold,
                        radius = 4.dp.toPx(),
                        center = Offset(size.width - 16.dp.toPx(), size.height / 2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = loadingText,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "DO NOT LEAVE THIS SCREEN",
                color = CosmicGold.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}
