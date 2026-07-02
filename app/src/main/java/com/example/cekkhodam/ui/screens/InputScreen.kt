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

    // Stars background generation
    val stars = remember {
        List(40) {
            Offset(Random.nextFloat(), Random.nextFloat()) to Random.nextFloat()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "StarPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "StarAlpha"
    )

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
        // Floating Stars
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { (offset, scale) ->
                drawCircle(
                    color = Color.White.copy(alpha = pulseAlpha * scale),
                    radius = 3.dp.toPx() * scale,
                    center = Offset(offset.x * size.width, offset.y * size.height)
                )
            }
        }

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
