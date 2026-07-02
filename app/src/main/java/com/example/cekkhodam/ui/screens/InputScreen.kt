package com.example.cekkhodam.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cekkhodam.theme.CosmicBgEnd
import com.example.cekkhodam.theme.CosmicBgStart
import com.example.cekkhodam.theme.CosmicGold
import com.example.cekkhodam.theme.GlassBorder
import com.example.cekkhodam.theme.GlassSurface
import com.example.cekkhodam.theme.PurpleSpark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    onSubmit: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("Select Birthday") }
    var showErrorMessage by remember { mutableStateOf(false) }

    // Fingerprint Scanner holding states
    var scanProgress by remember { mutableFloatStateOf(0f) }
    var isScanning by remember { mutableStateOf(false) }

    // Stars background generation (3D radial starfield: angle, speed depth, start distance)
    val stars = remember {
        List(60) {
            Triple(
                Random.nextFloat() * 2f * Math.PI.toFloat(), // angle
                0.4f + Random.nextFloat() * 0.6f,            // speed
                Random.nextFloat()                           // distanceOffset
            )
        }
    }

    // Animated stardust variables driven by a fail-safe coroutine clock
    var pulseAlpha by remember { mutableFloatStateOf(0.5f) }
    var driftOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            
            // Twinkle: sinusoidal cycle between 0.3f and 0.8f every 1.2 seconds
            val twinkleAngle = (elapsed % 1200) / 1200f * 2.0 * Math.PI
            pulseAlpha = 0.3f + 0.5f * ((Math.sin(twinkleAngle).toFloat() + 1f) / 2f)

            // Drift: cycle between 0f and 1f every 7 seconds
            driftOffset = (elapsed % 7000) / 7000f
            
            delay(16) // ~60fps target rate
        }
    }

    // Calendar initialization
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val monthStr = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
                val dayStr = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                dob = "$year-$monthStr-$dayStr"
                showErrorMessage = false
            },
            calendar.get(Calendar.YEAR) - 20,
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }
    }

    // Coroutine managing haptic pulses and progress during scanning
    LaunchedEffect(isScanning) {
        if (isScanning) {
            scanProgress = 0f
            while (isScanning && scanProgress < 1.0f) {
                delay(30)
                scanProgress += 0.015f
                // Accelerated haptic pulse feedback
                val hapticFrequency = (40 - (scanProgress * 30).toInt()).coerceAtLeast(10)
                if (System.currentTimeMillis() % hapticFrequency < 30) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
            if (scanProgress >= 1.0f) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                isScanning = false
                onSubmit(name, dob)
            }
        } else {
            scanProgress = 0f
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CosmicBgStart, CosmicBgEnd)
                )
            )
    ) {
        // Floating Stars with dynamic parallax drift using drawBehind for high-performance invalidation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val progress = driftOffset
                    val alphaVal = pulseAlpha
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    // Maximum distance from center to screen corner
                    val maxRadius = java.lang.Math.hypot(centerX.toDouble(), centerY.toDouble()).toFloat()

                    stars.forEach { (angle, speed, distanceOffset) ->
                        // Calculate radial progress distance from center (0f to 1f)
                        val distance = ((distanceOffset + progress * speed) % 1.0f)
                        
                        // Radial coordinates expansion outward
                        val x = centerX + kotlin.math.cos(angle.toDouble()).toFloat() * distance * maxRadius
                        val y = centerY + kotlin.math.sin(angle.toDouble()).toFloat() * distance * maxRadius
                        
                        // Star grows larger and brighter as it approaches
                        val radius = 1.dp.toPx() + 3.dp.toPx() * distance
                        val starAlpha = alphaVal * distance
                        
                        // 1. Large Outer Bloom Halo (low opacity, wide radius)
                        drawCircle(
                            color = Color.White.copy(alpha = (starAlpha * 0.12f).coerceIn(0f, 1f)),
                            radius = radius * 3.5f,
                            center = Offset(x, y)
                        )
                        // 2. Medium Inner Bloom Glow (medium opacity, mid radius)
                        drawCircle(
                            color = Color.White.copy(alpha = (starAlpha * 0.3f).coerceIn(0f, 1f)),
                            radius = radius * 1.8f,
                            center = Offset(x, y)
                        )
                        // 3. Main Star Core (solid brightness)
                        drawCircle(
                            color = Color.White.copy(alpha = starAlpha.coerceIn(0f, 1f)),
                            radius = radius,
                            center = Offset(x, y)
                        )
                    }
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🔮 CEK KHODAM",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CosmicGold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Discover your spiritual companion and check your energy alignment",
                color = Color.LightGray.copy(alpha = 0.8f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    showErrorMessage = false
                },
                label = { Text("Your Full Name", color = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CosmicGold,
                    unfocusedBorderColor = GlassBorder,
                    focusedContainerColor = GlassSurface,
                    unfocusedContainerColor = GlassSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DOB Selector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GlassSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                    .clickable { datePickerDialog.show() }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (dob == "Select Birthday") "Select Birthday" else "📅  $dob",
                    color = if (dob == "Select Birthday") Color.Gray else Color.White,
                    fontSize = 15.sp
                )
            }

            if (showErrorMessage) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Please enter your name and choose your birthday first!",
                    color = Color(0xFFE74C3C),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Glowing Haptic Scanner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isScanning) "Scanning Aura... ${(scanProgress * 100).toInt()}%" else "Hold to Scan Aura",
                    color = if (isScanning) CosmicGold else Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PurpleSpark.copy(alpha = 0.8f), CosmicBgStart)
                            )
                        )
                        .border(
                            2.dp,
                            if (isScanning) CosmicGold else GlassBorder,
                            CircleShape
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    if (name.isBlank() || dob == "Select Birthday") {
                                        showErrorMessage = true
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    } else {
                                        isScanning = true
                                        tryAwaitRelease()
                                        isScanning = false
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Fingerprint circular scanner drawing
                    Canvas(modifier = Modifier.size(90.dp)) {
                        val radius = size.minDimension / 2
                        // Background glow
                        drawCircle(
                            color = PurpleSpark.copy(alpha = 0.3f),
                            radius = radius
                        )
                        // Scanning progress ring
                        if (scanProgress > 0f) {
                            drawArc(
                                color = CosmicGold,
                                startAngle = -90f,
                                sweepAngle = 360f * scanProgress,
                                useCenter = false,
                                style = Stroke(width = 4.dp.toPx())
                            )
                        }
                    }

                    // Centered Mock Icon representing Fingerprint scanner
                    Text(
                        text = "👆",
                        fontSize = 36.sp
                    )
                }
            }
        }
    }
}
