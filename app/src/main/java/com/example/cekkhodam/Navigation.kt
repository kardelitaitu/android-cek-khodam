package com.example.cekkhodam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cekkhodam.ui.components.AdBanner
import com.example.cekkhodam.ui.components.InterstitialAd
import com.example.cekkhodam.ui.main.AppState
import com.example.cekkhodam.ui.main.MainViewModel
import com.example.cekkhodam.ui.screens.InputScreen
import com.example.cekkhodam.ui.screens.ResultScreen
import com.example.cekkhodam.ui.screens.RevealScreen

@Composable
fun MainNavigation(
    viewModel: MainViewModel = viewModel()
) {
    val state by viewModel.appState.collectAsState()
    val isLoaded by viewModel.isLoaded.collectAsState()

    if (!isLoaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0518)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Aligning cosmic elements...", color = Color.White)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        when (val currentState = state) {
            AppState.Input -> {
                InputScreen(
                    onSubmit = { name, dob ->
                        viewModel.submitQuery(name, dob)
                    }
                )
            }
            is AppState.InterstitialCheck -> {
                InterstitialAd(
                    adManager = viewModel.adManager,
                    onDismiss = {
                        viewModel.dismissCheckAd(currentState.name, currentState.dob)
                    },
                    onSkip = {
                        viewModel.skipCheckAd(currentState.name, currentState.dob)
                    }
                )
            }
            is AppState.Revealing -> {
                RevealScreen(
                    name = currentState.name,
                    dob = currentState.dob,
                    onRevealFinished = {
                        viewModel.revealResult(currentState.name, currentState.dob)
                    }
                )
            }
            is AppState.Result -> {
                ResultScreen(
                    name = currentState.name,
                    dob = currentState.dob,
                    khodam = currentState.khodam,
                    isFinancialUnlocked = currentState.isFinancialUnlocked,
                    isRomanticUnlocked = currentState.isRomanticUnlocked,
                    onUnlockRequest = { target ->
                        viewModel.requestUnlockProphecy(currentState, target)
                    },
                    onReset = {
                        viewModel.resetToInput()
                    },
                    onShareSuccess = {
                        viewModel.triggerShareReward()
                    }
                )
            }
            is AppState.InterstitialUnlock -> {
                InterstitialAd(
                    adManager = viewModel.adManager,
                    onDismiss = {
                        viewModel.dismissUnlockAd(currentState.currentResultState, currentState.targetToUnlock)
                    },
                    onSkip = {
                        viewModel.skipUnlockAd(currentState.currentResultState, currentState.targetToUnlock)
                    }
                )
            }
        }

        // Persistent bottom ad banner always anchored at the bottom
        AdBanner(
            adManager = viewModel.adManager,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
