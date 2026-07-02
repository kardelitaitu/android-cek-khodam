# Agent Instructions & Guidelines for Cek Khodam

This workspace houses the **Cek Khodam** Android application. When working on this project, please adhere to the following rules and instructions.

## Coding Style & Principles

- **Jetpack Compose**: Use modern, declarative UI patterns with Jetpack Compose. Prefer functional composition and standard styling parameters over custom wrapper objects unless highly reusable.
- **Mystical Aesthetic**: Maintain the dark cosmic design system (deep purples, neon golds, glassmorphic UI cards, subtle glowing card borders).
- **Kotlin Best Practices**: Use Kotlin coroutines for asynchronous tasks and follow clean programming conventions.

## Project Structure

- `com.example.cekkhodam.model`: Contains data models, asset loader, and simulated ad contracts.
- `com.example.cekkhodam.ui.screens`: Houses Compose screens (InputScreen, RevealScreen, ResultScreen).
- `com.example.cekkhodam.ui.components`: Holds reusable components (AdBanner, InterstitialAd).
- `com.example.cekkhodam.ui.theme`: Specifies cosmic color codes, theme, and styling variables.

## Key Development Rules

1. **Asset Parsing & Database Modulo**:
   - Any modifications to CSV assets in `app/src/main/assets/` must keep the sizes aligned with the math partition formulas: `elements` (10), `beasts` (30) combined to `0-299`; `jokes` (50) mapped to `300-349`; and `flowers` (100) mapped to `350-449`. Total partition range must sum up exactly to the selector modulus (450).
2. **Dynamic Image Naming Convention**:
   - The app loads visual assets from drawables dynamically by sanitizing the calculated Khodam name: `val sanitized = name.lowercase().replace(" ", "").replace("[^a-z0-9]".toRegex(), "")`.
   - Placed drawable images must be named as `khodam_[sanitized].png` or `khodam_[sanitized].webp`.
   - Always implement a styled character Emoji fallback if the resource lookup returns `0` (not found).
3. **Persisted Skip Ad Credits**:
   - Local skips count is stored in `SharedPreferences` under key `"skip_credits"` (default is 5, capped at 5). Consuming skips decrements by 1; sharing rewards resets/grants credits. Ensure VM calls the appropriate delegation methods on `AdManager`.
