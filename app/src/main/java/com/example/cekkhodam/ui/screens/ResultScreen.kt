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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
    language: String,
    onLanguageToggle: () -> Unit,
    onUnlockRequest: (String) -> Unit,
    onReset: () -> Unit,
    onShareSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Base color parsing
    val rawColor = android.graphics.Color.parseColor(
        khodam.glowColorHex.replace("0x", "#")
    )
    val elementColor = Color(rawColor)

    // Dialog state for full description view popup
    var showDescriptionDialog by remember { mutableStateOf(false) }

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

    // UI Translation Strings
    val localizedTitle = if (language == "EN") "✨ AURA REVEALED ✨" else "✨ AURA TERUNGKAP ✨"
    val powerLabel = if (language == "EN") "POWER" else "KEKUATAN"
    val mysticismLabel = if (language == "EN") "MYSTICISM" else "KEMISTISAN"
    val agilityLabel = if (language == "EN") "AGILITY" else "KELINCAHAN"
    
    val financialCardTitle = if (language == "EN") "💼 Financial" else "💼 Finansial"
    val romanticCardTitle = if (language == "EN") "💖 Romance" else "💖 Percintaan"
    val tapToRevealLabel = if (language == "EN") "Tap to Reveal" else "Ketuk untuk Membuka"
    val shareRewardLabel = if (language == "EN") "Share result to get +5 Skip Ad credits (Capped at 5)" else "Bagikan hasil untuk dapat +5 kredit Lewati Iklan (Maks 5)"
    val checkAnotherNameLabel = if (language == "EN") "Check Another Name" else "Cek Nama Lain"

    // Parse names dynamically: Wood (Kayu) -> EN: Wood, ID: Kayu.
    // Compound element-beast names like "Wood (Kayu) Tiger (Macan Putih)" are flipped to Indonesian grammar order: "Macan Putih Kayu".
    val localizedName = remember(khodam.name, language) {
        val enName = khodam.name.replace(Regex("\\([^)]*\\)"), "").replace("  ", " ").trim()
        if (language == "EN") {
            enName
        } else {
            val matches = Regex("\\(([^)]*)\\)").findAll(khodam.name).map { it.groupValues[1] }.toList()
            if (matches.size >= 2) {
                "${matches[1]} ${matches[0]}"
            } else if (matches.isNotEmpty()) {
                matches[0]
            } else {
                enName
            }
        }
    }

    fun translateProphecy(text: String): String {
        if (language == "EN") return text
        return when {
            text.contains("Steady growth awaits") -> "Pertumbuhan stabil menanti. Kesabaran Anda akan membuahkan hasil finansial."
            text.contains("bloom slowly but form") -> "Hubungan Anda akan berkembang perlahan namun berakar kuat."
            text.contains("Take bold risks now") -> "Ambil risiko berani sekarang! Energi dinamis akan melipatgandakan kekayaan."
            text.contains("Watch out for sudden") -> "Gairah yang membara. Waspadai argumen emosional, tetap tenang."
            text.contains("Income will flow smoothly") -> "Pendapatan akan mengalir lancar seperti air. Beradaptasi untuk meraih peluang."
            text.contains("flows with empathy") -> "Anda mengalir dengan empati. Sangat cocok dengan jiwa yang tenang."
            text.contains("Structure your budget") -> "Rapikan anggaran Anda. Tekad yang kuat akan membawa keuangan yang stabil."
            text.contains("protective and solid") -> "Anda pelindung yang kokoh. Hubungan terjamin, tetapi jangan terlalu kaku."
            text.contains("grounded approach brings") -> "Pendekatan membumi membawa kestabilan. Amankan tabungan keuangan Anda."
            text.contains("reliable and supportive") -> "Anda dapat diandalkan. Fondasi yang kokoh untuk cinta jangka panjang."
            text.contains("Unexpected windfalls") -> "Rezeki nomplok tak terduga. Bintang-bintang mendukung karir Anda."
            text.contains("stellar romance is written") -> "Koneksi bintang kosmik yang romantis telah tertulis di langit."
            text.contains("Opportunities come and go") -> "Peluang datang dan pergi dengan cepat. Tangkap angin rezeki Anda."
            text.contains("lighthearted and playful") -> "Cinta yang santai dan penuh canda. Jaga obrolan tetap segar."
            text.contains("Sudden financial breakthroughs") -> "Terobosan finansial mendadak. Ikuti badai menuju kemakmuran."
            text.contains("Electric chemistry") -> "Chemistry yang menyengat! Percikan cinta membara, jaga agar tidak korsleting."
            text.contains("financial paths are clear") -> "Jalur keuangan Anda terang benderang. Kejujuran membawa berkah."
            text.contains("pure and transparent") -> "Cinta yang murni dan transparan. Komunikasi terbuka memperkuat ikatan."
            text.contains("Opportunities lie hidden") -> "Peluang tersembunyi di kegelapan. Berinvestasi secara diam-diam."
            text.contains("Mysterious and alluring") -> "Misterius dan memikat. Jaga sedikit rahasia agar cinta tetap menarik."
            text.contains("Crispy cash flow") -> "Aliran uang renyah garing. Bisnis lucu membawa uang tak terduga."
            text.contains("Sweet and crispy") -> "Manis dan garing renyah. Anda lucu, menyenangkan, mudah dicintai."
            text.contains("Simple saving strategies") -> "Strategi menabung sederhana bekerja dengan baik."
            text.contains("Simple lifestyle saves") -> "Gaya hidup sederhana menghemat banyak uang."
            text.contains("Cheerful dates") -> "Kencan yang menyenangkan dan santai."
            text.contains("Warm and optimistic") -> "Hangat dan optimis. Membawa kebahagiaan dalam asmara."
            text.contains("A lucky catch") -> "Tangkapan beruntung. Anda membawa keberuntungan untuk pasangan."
            else -> {
                text.replace("High", "Tinggi")
                    .replace("Low", "Rendah")
                    .replace("Good", "Bagus")
                    .replace("Love", "Cinta")
                    .replace("Wealth", "Kekayaan")
                    .replace("returns", "hasil")
                    .replace("investments", "investasi")
                    .replace("investment", "investasi")
            }
        }
    }

    fun translateDescription(desc: String): String {
        if (language == "EN") return desc
        return desc
            .replace("which", "yang")
            .replace("influenced by", "dipengaruhi oleh")
            .replace("radiates a calm and growing energy", "memancarkan energi tenang dan bertumbuh")
            .replace("burns with high-octane passion", "membara dengan gairah yang kuat")
            .replace("flows smoothly and adapts", "mengalir lancar dan beradaptasi")
            .replace("is rigid and highly structured", "kaku dan sangat terstruktur")
            .replace("is grounded and stable", "membumi dan stabil")
            .replace("holds the ancient mystery of stars", "menyimpan misteri kuno bintang-bintang")
            .replace("moves swiftly and changes", "bergerak cepat dan berubah-ubah")
            .replace("strikes with sudden intensity", "menyambar dengan intensitas mendadak")
            .replace("radiates pure purity and clarity", "memancarkan kemurnian dan kejernihan")
            .replace("conceals secrets and movements", "menyembunyikan rahasia dan gerakan")
            .replace("represents", "melambangkan")
            .replace("protects its territory", "melindungi wilayahnya")
            .replace("sovereign power", "kekuasaan berdaulat")
            .replace("chaotic", "kacau")
            .replace("sweet-talking", "pandai merayu")
            .replace("rapid wealth acquisition", "meraih kekayaan dengan cepat")
    }

    // Dynamic sharing text builder based on current language
    val shareText = remember(isFinancialUnlocked, isRomanticUnlocked, language, localizedName) {
        val base = if (language == "EN") {
            "🔮 My Khodam is the **$localizedName**! ✨\n" +
            "Power: ${khodam.power}% | Mysticism: ${khodam.mysticism}%\n" +
            "Description: ${translateDescription(khodam.description)}\n"
        } else {
            "🔮 Pendamping Spiritual saya adalah **$localizedName**! ✨\n" +
            "Kekuatan: ${khodam.power}% | Kemistisan: ${khodam.mysticism}%\n" +
            "Deskripsi: ${translateDescription(khodam.description)}\n"
        }
        val finTitle = if (language == "EN") "Financial" else "Keuangan"
        val romTitle = if (language == "EN") "Romantic" else "Asmara"
        val fin = if (isFinancialUnlocked) "$finTitle: ${translateProphecy(khodam.financial)}\n" else ""
        val rom = if (isRomanticUnlocked) "$romTitle: ${translateProphecy(khodam.romantic)}\n" else ""
        val footer = if (language == "EN") "Check your Khodam now at Play Store! 🔮" else "Cek Khodam Anda sekarang di Play Store! 🔮"
        "$base$fin$rom\n$footer"
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
            val toastMsg = if (language == "EN") "Copied to clipboard & opening $label!" else "Disalin ke papan klip & membuka $label!"
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
        } else {
            val chooser = Intent.createChooser(intent, "Share Result via")
            context.startActivity(chooser)
            val toastMsg = if (language == "EN") "$label not found. Copied to clipboard & opening share menu." else "$label tidak ditemukan. Disalin ke papan klip & membuka menu bagikan."
            Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show()
        }
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

            // Upper Header with Title and Language Toggle (EN / ID)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = localizedTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CosmicGold,
                    letterSpacing = 2.sp
                )

                // EN / ID Language Switcher Toggle on Top Right
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GlassSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                        .clickable { onLanguageToggle() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EN",
                        color = if (language == "EN") CosmicGold else Color.Gray,
                        fontWeight = if (language == "EN") FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                    Text(
                        text = " / ",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "ID",
                        color = if (language == "ID") CosmicGold else Color.Gray,
                        fontWeight = if (language == "ID") FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shifting holographic cosmic rainbow outline ring
            val cardBrush = Brush.linearGradient(
                colors = listOf(
                    elementColor,
                    CosmicGold,
                    PurpleSpark,
                    Color(0xFF00E5FF), // Cyber Cyan/Rainbow mint
                    elementColor
                ),
                start = Offset(sheenOffset, sheenOffset),
                end = Offset(sheenOffset + 500f, sheenOffset + 500f)
            )

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
                    // Dynamic Image Asset loader (uses sanitized EN clean name)
                    val cleanName = khodam.name.replace(Regex("\\([^)]*\\)"), "").replace("  ", " ").trim()
                    val sanitizedName = cleanName.lowercase().replace(" ", "").replace("[^a-z0-9]".toRegex(), "")
                    val imageResId = remember(sanitizedName) {
                        context.resources.getIdentifier("khodam_$sanitizedName", "drawable", context.packageName)
                    }

                    if (imageResId != 0) {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = imageResId),
                            contentDescription = localizedName,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, elementColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        )
                    } else {
                        val emoji = when (khodam.category) {
                            "Joke" -> "🤪"
                            "Flower" -> "🌸"
                            else -> "🐉"
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(elementColor.copy(alpha = 0.15f))
                                .border(1.dp, elementColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 64.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Localized Companion Name
                    Text(
                        text = localizedName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = CosmicGold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Localized Description (Clickable to open full dialog, limited height for card space)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 48.dp)
                            .verticalScroll(rememberScrollState())
                            .clickable { showDescriptionDialog = true }
                    ) {
                        Text(
                            text = translateDescription(khodam.description),
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Side-by-Side Stat Boxes to reduce height and make room for the large image
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBox(label = powerLabel, progress = powerProgress.value, color = elementColor, modifier = Modifier.weight(1f))
                        StatBox(label = mysticismLabel, progress = mysticismProgress.value, color = elementColor, modifier = Modifier.weight(1f))
                        StatBox(label = agilityLabel, progress = agilityProgress.value, color = elementColor, modifier = Modifier.weight(1f))
                    }
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
                        title = financialCardTitle,
                        content = translateProphecy(khodam.financial),
                        isUnlocked = isFinancialUnlocked,
                        elementColor = elementColor,
                        onUnlockClick = { onUnlockRequest("FINANCIAL") },
                        tapToRevealLabel = tapToRevealLabel
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ProphecyCard(
                        title = romanticCardTitle,
                        content = translateProphecy(khodam.romantic),
                        isUnlocked = isRomanticUnlocked,
                        elementColor = elementColor,
                        onUnlockClick = { onUnlockRequest("ROMANTIC") },
                        tapToRevealLabel = tapToRevealLabel
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social Sharing Buttons Row
            Text(
                text = shareRewardLabel,
                color = Color.Gray,
                fontSize = 10.sp,
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

            // Check Another Name Button
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
                        text = checkAnotherNameLabel,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }

        // Full description popup dialog
        if (showDescriptionDialog) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showDescriptionDialog = false }
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GlassSurface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .border(2.dp, elementColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = localizedName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = CosmicGold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .heightIn(max = 280.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = translateDescription(khodam.description),
                                color = Color.White,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Cek Khodam Description", translateDescription(khodam.description))
                                    clipboard.setPrimaryClip(clip)
                                    val toastMsg = if (language == "EN") "Description copied!" else "Deskripsi disalin!"
                                    android.widget.Toast.makeText(context, toastMsg, android.widget.Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GlassSurface),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
                            ) {
                                Text(
                                    text = if (language == "EN") "Copy" else "Salin",
                                    color = CosmicGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { showDescriptionDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = GlassSurface),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
                            ) {
                                Text(
                                    text = if (language == "EN") "Close" else "Tutup",
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
}

@Composable
fun StatBox(label: String, progress: Float, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = Color.LightGray,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${(progress * 100).toInt()}%",
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
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
    onUnlockClick: () -> Unit,
    tapToRevealLabel: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp) // Fixed smaller height
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp) // Smaller padding to maximize layout efficiency
                    .then(if (!isUnlocked) Modifier.blur(8.dp) else Modifier)
            ) {
                Text(
                    text = title,
                    color = CosmicGold,
                    fontSize = 12.sp, // Optimized font size
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Scrollable text area inside fixed-height box to handle long texts gracefully
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = content,
                        color = Color.White,
                        fontSize = 11.sp, // Sized down to fit smaller box dimensions
                        lineHeight = 15.sp
                    )
                }
            }

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
                            modifier = Modifier.size(20.dp) // Scaled down lock icon
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tapToRevealLabel,
                            color = Color.White,
                            fontSize = 10.sp,
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
