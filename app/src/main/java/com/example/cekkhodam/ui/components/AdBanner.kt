package com.example.cekkhodam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cekkhodam.model.AdManager
import com.example.cekkhodam.theme.CosmicGold
import com.example.cekkhodam.theme.GlassBorder
import com.example.cekkhodam.theme.GlassSurface
import com.example.cekkhodam.theme.PurpleSpark

@Composable
fun AdBanner(
    adManager: AdManager,
    modifier: Modifier = Modifier
) {
    val adContent by adManager.bannerAd.collectAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF140D26), Color(0xFF090412))
                )
            )
            .border(1.dp, GlassBorder)
            .clickable {
                adManager.rotateBannerAd()
            }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // "Ad" badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(CosmicGold)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "AD",
                    color = Color.Black,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Ad Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = adContent.title,
                    color = CosmicGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = adContent.description,
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // CTA Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PurpleSpark, Color(0xFFF15F79))
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = adContent.cta,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
