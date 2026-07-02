package com.example.cekkhodam.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

enum class MediationProvider { ADMOB, META, UNITY_ADS, APPLOVIN, MOCK }

data class MockAdContent(
    val title: String,
    val description: String,
    val cta: String
)

interface AdManager {
    val bannerAd: StateFlow<MockAdContent>
    val skipCredits: StateFlow<Int>
    fun rotateBannerAd()
    fun consumeSkipCredit(): Boolean
    fun rewardShareCredits()
    fun getActiveProvider(): MediationProvider
    fun setProvider(provider: MediationProvider)
}

class MockAdManagerImpl(private val onCreditsChanged: (Int) -> Unit, initialCredits: Int) : AdManager {

    private val adsList = listOf(
        MockAdContent("🔮 Aura Cleansing 50% OFF", "Clear negative energy and invite good luck today!", "Call Now"),
        MockAdContent("🦁 Feed Your White Tiger", "Get premium celestial tiger kibbles for your Khodam.", "Shop Now"),
        MockAdContent("🐊 Anti-Crocodile Talisman", "Guaranteed protection from sweet-talking land crocodiles.", "Get 1 Free"),
        MockAdContent("🏎️ Racing Shroud Upgrades", "Tuning packages for your local spirits. Boost agility stat!", "Upgrade"),
        MockAdContent("🌾 Lavender Cosmic Tea", "Sip cosmic tranquility and double your romance energy.", "Order Tea"),
        MockAdContent("📱 Play 'Cek Khodam 2'", "Pre-register for the multiplayer spiritual arena sequel!", "Register")
    )

    private val _bannerAd = MutableStateFlow(adsList.first())
    override val bannerAd: StateFlow<MockAdContent> = _bannerAd.asStateFlow()

    private val _skipCredits = MutableStateFlow(initialCredits)
    override val skipCredits: StateFlow<Int> = _skipCredits.asStateFlow()

    private var currentProvider = MediationProvider.MOCK

    override fun rotateBannerAd() {
        val nextAd = adsList[Random.nextInt(adsList.size)]
        _bannerAd.value = nextAd
    }

    override fun consumeSkipCredit(): Boolean {
        if (_skipCredits.value > 0) {
            _skipCredits.value -= 1
            onCreditsChanged(_skipCredits.value)
            return true
        }
        return false
    }

    override fun rewardShareCredits() {
        // Capped at maximum of 5 active credits
        val newCredits = (_skipCredits.value + 5).coerceAtMost(5)
        _skipCredits.value = newCredits
        onCreditsChanged(newCredits)
    }

    override fun getActiveProvider(): MediationProvider = currentProvider

    override fun setProvider(provider: MediationProvider) {
        currentProvider = provider
    }
}
