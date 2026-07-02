package com.example.cekkhodam.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cekkhodam.model.Khodam
import com.example.cekkhodam.theme.CosmicBgEnd
import com.example.cekkhodam.theme.CosmicBgStart
import com.example.cekkhodam.theme.CosmicGold
import com.example.cekkhodam.theme.GlassBorder
import com.example.cekkhodam.theme.GlassSurface
import com.example.cekkhodam.theme.PurpleSpark

@Composable
fun ResultScreen(
    name: String,
    dob: String,
    khodam: Khodam,
    isFinancialUnlocked: Boolean,
    isRomanticUnlocked: Boolean,
    onUnlockRequest: (String) -> Unit,
    onReset: () -> Unit,
    onShareSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Base color parsing
    val rawColor = android.graphics.Color.parseColor(
        khodam.glowColorHex.replace("0x", "#")
    )
    val elementColor = Color(rawColor)

    // Animated Attribute Bars
    val powerProgress = remember { Animatable(0f) }
    val mysticismProgress = remember { Animatable(0f) }
    val agilityProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        powerProgress.animateTo(
            targetValue = khodam.power.toFloat() / 100f,
            animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
        )
    }
    LaunchedEffect(Unit) {
        mysticismProgress.animateTo(
            targetValue = khodam.mysticism.toFloat() / 100f,
            animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
        )
    }
    LaunchedEffect(Unit) {
        agilityProgress.animateTo(
            targetValue = khodam.agility.toFloat() / 100f,
            animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
        )
    }

    // Iridescent moving sheen animation for fully unlocked holographic cards
    val infiniteTransition = rememberInfiniteTransition(label = "HoloSheen")
    val sheenOffset by infiniteTransition.animateFloat(
        initialValue = -500f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SheenOffset"
    )

    // Dynamic sharing text builder
    val shareText = remember(isFinancialUnlocked, isRomanticUnlocked) {
        val base = "🔮 My Khodam is the **${khodam.name}**! ✨\n" +
                "Power: ${khodam.power}% | Mysticism: ${khodam.mysticism}%\n" +
                "Description: ${khodam.description}\n"
        val fin = if (isFinancialUnlocked) "Financial Prophecy: ${khodam.financial}\n" else ""
        val rom = if (isRomanticUnlocked) "Romantic Prophecy: ${khodam.romantic}\n" else ""
        "$base$fin$rom\nCheck your Khodam now at Play Store! 🔮"
    }

    // Explicit package check and share dispatcher helper
    fun shareToSocialApp(packageName: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Cek Khodam", shareText)
        clipboard.setPrimaryClip(clip)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        val pm = context.packageManager
        val isInstalled = try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        if (isInstalled) {
            intent.setPackage(packageName)
            context.startActivity(intent)
            Toast.makeText(context, "Copied to clipboard & opening $label!", Toast.LENGTH_SHORT).show()
        } else {
            // General share chooser fallback
            val chooser = Intent.createChooser(intent, "Share Result via")
            context.startActivity(chooser)
            Toast.makeText(context, "$label not found. Copied to clipboard & opening share menu.", Toast.LENGTH_LONG).show()
        }
        // Grant +5 skip credits reward
        onShareSuccess()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Upper Title
            Text(
                text = "✨ AURA REVEALED ✨",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CosmicGold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Calculated for $name",
                color = Color.LightGray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            // Holographic Cosmic Card Layout
            val cardBrush = if (isFinancialUnlocked && isRomanticUnlocked) {
                // Iridescent holographic moving sweep
                Brush.linearGradient(
                    colors = listOf(elementColor, CosmicGold, PurpleSpark, elementColor),
                    start = Offset(sheenOffset, 0f),
                    end = Offset(sheenOffset + 300f, 400f)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(elementColor, elementColor.copy(alpha = 0.5f))
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = GlassSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, cardBrush, RoundedCornerShape(20.dp))
                    .padding(1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Dynamic Image Asset loader with Emoji fallback
                    val cleanName = khodam.name.replace(Regex("\\([^)]*\\)"), "").trim()
                    val sanitizedName = cleanName.lowercase().replace(" ", "").replace("[^a-z0-9]".toRegex(), "")
                    val imageResId = remember(sanitizedName) {
                        context.resources.getIdentifier("khodam_$sanitizedName", "drawable", context.packageName)
                    }

                    if (imageResId != 0) {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = imageResId),
                            contentDescription = khodam.name,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(1.dp, elementColor.copy(alpha = 0.5f), CircleShape)
                        )
                    } else {
                        val emoji = when (khodam.category) {
                            "Joke" -> "🤪"
                            "Flower" -> "🌸"
                            else -> "🐉"
                        }
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(elementColor.copy(alpha = 0.2f))
                                .border(1.dp, elementColor.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 32.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Companion Name
                    Text(
                        text = khodam.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = CosmicGold,
                        textAlign = TextAlign.Center
                    )

                    // Category Badge
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(elementColor.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = khodam.category.uppercase(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Character Description
                    Text(
                        text = khodam.description,
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Attribute Stats
                    AttributeRow("POWER", powerProgress.value, elementColor)
                    AttributeRow("MYSTICISM", mysticismProgress.value, elementColor)
                    AttributeRow("AGILITY", agilityProgress.value, elementColor)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Locked Prophecies Segment Row (Side-by-Side)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ProphecyCard(
                        title = "💼 Financial",
                        content = khodam.financial,
                        isUnlocked = isFinancialUnlocked,
                        elementColor = elementColor,
                        onUnlockClick = { onUnlockRequest("FINANCIAL") }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ProphecyCard(
                        title = "💖 Romance",
                        content = khodam.romantic,
                        isUnlocked = isRomanticUnlocked,
                        elementColor = elementColor,
                        onUnlockClick = { onUnlockRequest("ROMANTIC") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social Sharing Buttons Row
            Text(
                text = "Share result to get +5 Skip Ad credits (Capped at 5)",
                color = Color.Gray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ShareIconButton("WA", "com.whatsapp", "WhatsApp", ::shareToSocialApp)
                ShareIconButton("FB", "com.facebook.katana", "Facebook", ::shareToSocialApp)
                ShareIconButton("IG", "com.instagram.android", "Instagram", ::shareToSocialApp)
                ShareIconButton("TikTok", "com.zhiliaoapp.musically", "TikTok", ::shareToSocialApp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Try Again
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = GlassSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = CosmicGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Check Another Name",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(120.dp)) // Padding to avoid overlap with bottom banner ad
        }
    }
}

@Composable
fun AttributeRow(label: String, progress: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = "${(progress * 100).toInt()}%", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = progress,
            color = color,
            trackColor = Color.White.copy(alpha = 0.1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .padding(vertical = 1.dp)
        )
    }
}

@Composable
fun ProphecyCard(
    title: String,
    content: String,
    isUnlocked: Boolean,
    elementColor: Color,
    onUnlockClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .then(if (!isUnlocked) Modifier.blur(8.dp) else Modifier)
            ) {
                Text(
                    text = title,
                    color = CosmicGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = content,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            // Lock Overlay if not unlocked yet
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { onUnlockClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = CosmicGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to Reveal",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShareIconButton(
    label: String,
    packageName: String,
    nameLabel: String,
    onShareClick: (String, String) -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(GlassSurface)
            .border(1.dp, GlassBorder, CircleShape)
            .clickable { onShareClick(packageName, nameLabel) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when (label) {
                    "WA" -> "🟢"
                    "FB" -> "🔵"
                    "IG" -> "📸"
                    else -> "🎵"
                },
                fontSize = 16.sp
            )
            Text(
                text = label,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
