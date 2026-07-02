package com.example.cekkhodam.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.foundation.layout.Row
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

data class WarpStar(
    val angle: Float,
    val speed: Float,
    val distanceOffset: Float,
    val color: Color
)

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

    // Gyroscope/Accelerometer tilt states for VR-like 3D parallax effect
    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        val listener = object : SensorEventListener {
            private var lastYaw = 0f
            private var hasInitYaw = false

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    
                    val yaw = orientation[0]   // azimuth/yaw
                    val pitch = orientation[1] // pitch/tilt
                    
                    if (!hasInitYaw) {
                        lastYaw = yaw
                        hasInitYaw = true
                    }
                    
                    // Smooth out relative yaw changes to center starting position and prevent jumps
                    var yawDiff = yaw - lastYaw
                    if (yawDiff > Math.PI) yawDiff -= (2 * Math.PI).toFloat()
                    if (yawDiff < -Math.PI) yawDiff += (2 * Math.PI).toFloat()
                    lastYaw = yaw

                    tiltX = (tiltX + yawDiff * 1.8f).coerceIn(-15f, 15f) * 0.97f
                    tiltY = tiltY * 0.85f + pitch * 0.15f
                    
                } else if (event.sensor.type == Sensor.TYPE_GYROSCOPE && rotationVector == null) {
                    tiltX = (tiltX + event.values[1] * 0.25f).coerceIn(-15f, 15f) * 0.97f
                    tiltY = (tiltY + event.values[0] * 0.25f).coerceIn(-15f, 15f) * 0.97f
                } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER && rotationVector == null && gyroscope == null) {
                    tiltX = tiltX * 0.9f + event.values[0] * 0.1f
                    tiltY = tiltY * 0.9f + (event.values[1] - 5f) * 0.1f
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        when {
            rotationVector != null -> {
                sensorManager.registerListener(listener, rotationVector, SensorManager.SENSOR_DELAY_GAME)
            }
            gyroscope != null -> {
                sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_GAME)
            }
            accelerometer != null -> {
                sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            }
        }
        
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val starColors = remember {
        listOf(
            Color.White,
            Color(0xFF80DEEA), // Cyber Cyan
            Color(0xFFFFE082), // Cosmic Amber Gold
            Color(0xFFE1BEE7), // Soft Cosmic Violet
            Color(0xFF90CAF9)  // Deep Sky Blue
        )
    }

    // Stars background generation (3D radial starfield coordinates: angle, speed depth, start distance, color)
    val stars = remember {
        List(85) {
            WarpStar(
                angle = Random.nextFloat() * 2f * Math.PI.toFloat(),
                speed = 0.15f + Random.nextFloat() * 0.35f, // Slow approach velocity
                distanceOffset = Random.nextFloat(),
                color = starColors[Random.nextInt(starColors.size)]
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

            // Drift: Continuous infinite accumulation (slowed down for very gentle approach movement)
            driftOffset = elapsed / 22000f
            
            delay(16) // ~60fps target rate
        }
    }

    // Custom Date Picker states (Wheel scrollable native Compose inputs)
    var showCustomDatePicker by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf("15") }
    var selectedMonth by remember { mutableStateOf("06") }
    var selectedYear by remember { mutableStateOf("2000") }

    val daysList = remember { (1..31).map { if (it < 10) "0$it" else "$it" } }
    val monthsList = remember { (1..12).map { if (it < 10) "0$it" else "$it" } }
    val yearsList = remember { (1940..2026).map { "$it" } }

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
                    val alphaVal = pulseAlpha
                    val progress = driftOffset
                    
                    // VR-like panning shifts the center of travel direction dynamically based on device rotation
                    val centerX = size.width / 2f - tiltX * 18.dp.toPx()
                    val centerY = size.height / 2f - tiltY * 18.dp.toPx()
                    
                    // Maximum distance from center to screen corner from the current shifted origin
                    val maxRadius = java.lang.Math.hypot(centerX.toDouble(), centerY.toDouble()).toFloat()

                    stars.forEach { star ->
                        // Calculate radial progress distance from center (0f to 1f)
                        val distance = ((star.distanceOffset + progress * star.speed) % 1.0f)
                        
                        // Coordinates emerging and expanding outward from shifted center
                        val x = centerX + kotlin.math.cos(star.angle.toDouble()).toFloat() * distance * maxRadius
                        val y = centerY + kotlin.math.sin(star.angle.toDouble()).toFloat() * distance * maxRadius
                        
                        // Star size scales up slowly as it approaches
                        val radius = (1.dp.toPx() + 3.dp.toPx() * distance) * 0.75f
                        val starAlpha = alphaVal * distance

                        // 1. Large Outer Bloom Halo (low opacity)
                        drawCircle(
                            color = star.color.copy(alpha = (starAlpha * 0.15f).coerceIn(0f, 1f)),
                            radius = radius * 3.5f,
                            center = Offset(x, y)
                        )
                        // 2. Medium Inner Glow
                        drawCircle(
                            color = star.color.copy(alpha = (starAlpha * 0.4f).coerceIn(0f, 1f)),
                            radius = radius * 1.8f,
                            center = Offset(x, y)
                        )
                        // 3. Core
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
                    .clickable { showCustomDatePicker = true }
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

        // Custom native Wheel Date Picker Dialog overlay
        if (showCustomDatePicker) {
            Dialog(
                onDismissRequest = { showCustomDatePicker = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                androidx.compose.material3.Card(
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = GlassSurface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .border(2.dp, CosmicGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📅 Select Birthday",
                            color = CosmicGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WheelSelector(
                                items = daysList,
                                selectedItem = selectedDay,
                                onItemSelected = { selectedDay = it },
                                modifier = Modifier.weight(1f)
                            )
                            WheelSelector(
                                items = monthsList,
                                selectedItem = selectedMonth,
                                onItemSelected = { selectedMonth = it },
                                modifier = Modifier.weight(1f)
                            )
                            WheelSelector(
                                items = yearsList,
                                selectedItem = selectedYear,
                                onItemSelected = { selectedYear = it },
                                modifier = Modifier.weight(1.2f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        androidx.compose.material3.Button(
                            onClick = {
                                dob = "$selectedYear-$selectedMonth-$selectedDay"
                                showErrorMessage = false
                                showCustomDatePicker = false
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GlassSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .border(1.dp, GlassBorder, RoundedCornerShape(23.dp))
                        ) {
                            Text(
                                text = "Confirm",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WheelSelector(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(selectedItem) {
        val index = items.indexOf(selectedItem)
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    Box(
        modifier = modifier
            .height(150.dp)
            .background(Color.Black.copy(alpha = 0.3f))
            .border(1.dp, GlassBorder, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Center selection bar highlighting borders
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .border(1.dp, CosmicGold.copy(alpha = 0.25f))
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 56.dp)
        ) {
            items(items) { item ->
                val isSelected = item == selectedItem
                Text(
                    text = item,
                    color = if (isSelected) CosmicGold else Color.Gray,
                    fontSize = if (isSelected) 18.sp else 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected(item) }
                        .padding(vertical = 6.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
