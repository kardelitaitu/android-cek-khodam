package com.example.cekkhodam.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.cekkhodam.model.AdManager
import com.example.cekkhodam.theme.CosmicBgEnd
import com.example.cekkhodam.theme.CosmicBgStart
import com.example.cekkhodam.theme.CosmicGold
import com.example.cekkhodam.theme.GlassBorder
import com.example.cekkhodam.theme.GlassSurface
import com.example.cekkhodam.theme.PurpleSpark
import kotlinx.coroutines.delay

@Composable
fun InterstitialAd(
    adManager: AdManager,
    onDismiss: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val skipCredits by adManager.skipCredits.collectAsState()
    var secondsRemaining by remember { mutableIntStateOf(1) }
    var canClose by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
        canClose = 1
    }

    Dialog(
        onDismissRequest = {
            if (canClose == 1) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CosmicBgStart, CosmicBgEnd)
                    )
                )
        ) {
            // Close / Skip header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip Button using credits
                if (skipCredits > 0) {
                    Button(
                        onClick = onSkip,
                        colors = ButtonDefaults.buttonColors(containerColor = GlassSurface),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Credit",
                                tint = CosmicGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Skip (1 Credit. Left: $skipCredits)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Normal close button or timer
                if (canClose == 1) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = secondsRemaining.toString(),
                            color = CosmicGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Main Ad Body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(top = 80.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Big funny app mockup visual
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PurpleSpark, CosmicGold)
                            )
                        )
                        .border(2.dp, GlassBorder, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎮\nSPIN\nSPIRITS",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "🔥 Tame The Legendary Fire Dragon! 🔥",
                    color = CosmicGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "A brand new spiritual RPG where you capture and upgrade over 300+ elements. Play for free today!",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Install Button
                Button(
                    onClick = onDismiss, // Mock installation dismissal
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleSpark
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp)
                        .border(1.dp, CosmicGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                ) {
                    Text(
                        text = "INSTALL FOR FREE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Contains simulated items • Ads support development",
                    color = Color.Gray,
                    fontSize = 9.sp
                )
            }
        }
    }
}
