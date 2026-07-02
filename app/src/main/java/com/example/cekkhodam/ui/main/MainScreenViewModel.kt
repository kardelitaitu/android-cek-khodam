package com.example.cekkhodam.ui.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cekkhodam.model.AdManager
import com.example.cekkhodam.model.Beast
import com.example.cekkhodam.model.Element
import com.example.cekkhodam.model.Flower
import com.example.cekkhodam.model.Joke
import com.example.cekkhodam.model.Khodam
import com.example.cekkhodam.model.KhodamEngine
import com.example.cekkhodam.model.MockAdManagerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AppState {
    object Input : AppState()
    data class InterstitialCheck(val name: String, val dob: String) : AppState()
    data class Revealing(val name: String, val dob: String) : AppState()
    data class Result(
        val name: String,
        val dob: String,
        val khodam: Khodam,
        val isFinancialUnlocked: Boolean = false,
        val isRomanticUnlocked: Boolean = false
    ) : AppState()
    data class InterstitialUnlock(
        val currentResultState: Result,
        val targetToUnlock: String // "FINANCIAL" or "ROMANTIC"
    ) : AppState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("cek_khodam_prefs", Context.MODE_PRIVATE)

    // Data lists parsed from CSV assets
    private var elementsList = emptyList<Element>()
    private var beastsList = emptyList<Beast>()
    private var jokesList = emptyList<Joke>()
    private var flowersList = emptyList<Flower>()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    // Navigation and screen AppState flow
    private val _appState = MutableStateFlow<AppState>(AppState.Input)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    // Ad Manager delegation
    val adManager: AdManager = MockAdManagerImpl(
        onCreditsChanged = { credits ->
            prefs.edit().putInt("skip_credits", credits).apply()
        },
        initialCredits = prefs.getInt("skip_credits", 5)
    )

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            elementsList = KhodamEngine.loadElements(context)
            beastsList = KhodamEngine.loadBeasts(context)
            jokesList = KhodamEngine.loadJokes(context)
            flowersList = KhodamEngine.loadFlowers(context)
            _isLoaded.value = true
        }
    }

    // Input Screen submit action
    fun submitQuery(name: String, dob: String) {
        if (name.isBlank()) return
        _appState.value = AppState.InterstitialCheck(name, dob)
    }

    // Bypass/skip ad check using 1 skip credit
    fun skipCheckAd(name: String, dob: String) {
        if (adManager.consumeSkipCredit()) {
            startReveal(name, dob)
        }
    }

    // Dismiss check ad and proceed to cosmic reveal
    fun dismissCheckAd(name: String, dob: String) {
        startReveal(name, dob)
    }

    private fun startReveal(name: String, dob: String) {
        _appState.value = AppState.Revealing(name, dob)
    }

    // Complete the reveal and calculate the final Khodam
    fun revealResult(name: String, dob: String) {
        val khodam = KhodamEngine.calculateKhodam(
            name = name,
            dob = dob,
            elements = elementsList,
            beasts = beastsList,
            jokes = jokesList,
            flowers = flowersList
        )
        _appState.value = AppState.Result(name, dob, khodam)
    }

    // Click view locked prophecy trigger
    fun requestUnlockProphecy(currentResult: AppState.Result, type: String) {
        _appState.value = AppState.InterstitialUnlock(currentResult, type)
    }

    // Bypass/skip unlock ad using 1 skip credit
    fun skipUnlockAd(currentResult: AppState.Result, type: String) {
        if (adManager.consumeSkipCredit()) {
            performUnlock(currentResult, type)
        }
    }

    // Dismiss unlock ad and reveal the prophecy details
    fun dismissUnlockAd(currentResult: AppState.Result, type: String) {
        performUnlock(currentResult, type)
    }

    private fun performUnlock(currentResult: AppState.Result, type: String) {
        val updatedResult = if (type == "FINANCIAL") {
            currentResult.copy(isFinancialUnlocked = true)
        } else {
            currentResult.copy(isRomanticUnlocked = true)
        }
        _appState.value = updatedResult
    }

    // Cancel unlock ad screen
    fun cancelUnlockAd(currentResult: AppState.Result) {
        _appState.value = currentResult
    }

    // Go back to input screen
    fun resetToInput() {
        adManager.rotateBannerAd()
        _appState.value = AppState.Input
    }

    // Social share reward system
    fun triggerShareReward() {
        adManager.rewardShareCredits()
    }
}
