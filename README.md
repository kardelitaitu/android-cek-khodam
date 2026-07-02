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
* **Aura Scanner**: Glowing button requiring a 2-second hold with accelerating tactile haptic vibrations.
* **Holographic Iridescent Sheen**: Moving rainbow light reflection applied dynamically to card borders once all prophecies are unlocked.
* **Smart Dynamic Image Fallback**: Resolves visual cards dynamically using sanitized names. If the generated PNG/WebP isn't present in resources, it defaults to a stylized emoji.

---

## Getting Started

### 1. Requirements
* Android Studio (Koala/Ladybug or later)
* JDK 17 (recommended to use the JBR bundled inside Android Studio)
* Gradle 9.1.0 (pre-configured)

### 2. How to Run
1. Open Android Studio and choose **Open Project**.
2. Select the `android-cek-khodam` root directory.
3. Once the Gradle build finishes syncing, connect an Android device with **USB Debugging** enabled, or start a virtual emulator.
4. Click the green **Run (Play)** button in Android Studio, or execute in your command line:
   ```bash
   ./gradlew installDebug
   ```

### 3. Adding ComfyUI Generated Image Assets
1. Generate your 512x512 illustrations locally in ComfyUI (e.g., using an RTX 5070 Ti).
2. Rename the image to lowercase, removing spaces and special symbols, prefixed with `khodam_`:
   * *Example*: **Wood Tiger** -> `khodam_woodtiger.webp`
   * *Example*: **Batu Bata Merah** -> `khodam_batubatamerah.png`
3. Copy the file and paste it into the project's drawable folder:
   📁 `app/src/main/res/drawable/`
4. Re-run or build the app. The fallback emoji will automatically be replaced by your custom artwork!


