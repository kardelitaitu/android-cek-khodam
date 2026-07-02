# Cek Khodam

Cek Khodam is a modern, premium Android application that reveals your spiritual companion (Khodam) based on your Name and Date of Birth. Inspired by zodiacs, tarot, and ancient prophecies, this app uses a cosmic theme to deliver a highly interactive and engaging user experience.

## Features

- **Mystical Analysis**: Enter your name and select your Date of Birth to reveal your unique Khodam.
- **Deterministic Algorithm**: The same name and Date of Birth combination will always result in the same Khodam.
- **Premium UI/UX**: Dark cosmic theme with beautiful gradients, glassmorphism, and animations.
- **Detailed Prophecy & Attributes**: Learn about your Khodam's element, power levels, and mystical characteristics.
- **Persistent Bottom Ad Ribbon**: An elegant ribbon at the bottom of the screen displaying relevant (mocked) ads.

## Tech Stack

- **Platform**: Android
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Build Tool**: Gradle (AGP 9)
- **Architecture**: MVI/MVVM clean layout

## Technical Architecture

### 1. Database Asset Structure
The application loads assets dynamically at runtime from four local CSV files:
* `elements.csv` (10 rows): Elemental aura properties (Wood, Fire, Water, etc.).
* `beasts.csv` (30 rows): Base creature properties and stats.
* `jokes.csv` (50 rows): Standalone humor/meme objects.
* `flowers.csv` (100 rows): Standalone floral species.

### 2. Deterministic Partition Selection
The app generates a SHA-256 hash from the combined `"${name}_${dob}"` seed, generating a selector index `V = hash.mod(450)`. Results are partitioned as follows:
- **0 to 299**: Element-Beast combination (`V % 10` for Element, `(V / 10) % 30` for Beast).
- **300 to 349**: Flat Joke entry (`(V - 300) % 50`).
- **350 to 449**: Flat Flower entry (`(V - 350) % 100`).

### 3. MVI State Flow
The navigation and UI states are modeled via a unified viewmodel state machine:
* `Input`: The main landing interface.
* `InterstitialCheck`: Renders the initial full-screen ad.
* `Revealing`: Animates the cosmic compass scan.
* `Result`: Displays stats, details, and locked prophecies.
* `InterstitialUnlock`: Renders full-screen ads when unlocking.

### 4. Advanced Interactive Features
* **Aura Scanner**: Glowing button requiring a 2-second hold with accelerating haptic vibrations.
* **Easter Eggs**: Checks name matches (e.g. *Windah Basudara*) to display custom static overrides.
* **Holographic Shader**: Iridescent canvas shader applied to card gradients when both prophecies are unlocked.

