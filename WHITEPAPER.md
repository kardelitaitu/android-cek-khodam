# Cek Khodam: Whitepaper & Product Specification

## 1. Executive Summary

"Cek Khodam" (Spiritual Companion Checker) is a mobile-first lifestyle application targeting the cultural interest in personal spirituality, personality profiles, and horoscope-like prophecies. By combining traditional Indonesian mystical lore with name-hashing mathematics, the app provides a highly engaging, deterministic horoscope experience.

## 2. Mathematical Algorithm (Partitioned Pool Selection)

To support distinct pools of Khodams (dynamic elements/beasts, flat joke items, and flat flower types) and allow easy expansion, the app uses a partitioned selection algorithm.

Let \(N\) be the input name string, and \(D, M, Y\) be the Day, Month, and Year of birth.
We compute the hash of the combined normalized input:
\[H = \text{SHA-256}(\text{normalize}(N) + \text{format}(D, M, Y))\]
Let \(V\) be the index selector:
\[V = \text{BigInteger}(H) \pmod{450}\]

### 2.1 Pool 1: Element-Beast Combination (\(0 \le V < 300\))
If \(V\) falls in this range, we select a combined Element and Beast:
- **Element Index** \(I_{\text{element}}\):
  \[I_{\text{element}} = V \pmod{10}\]
- **Beast Index** \(I_{\text{beast}}\):
  \[I_{\text{beast}} = \lfloor V / 10 \rfloor \pmod{30}\]
- **Result**: `elements[I_element] + " " + beasts[I_beast]` (e.g. *"Wood Tiger"*).

### 2.2 Pool 2: Jokes (\(300 \le V < 350\))
If \(V\) falls in this range, we select a flat Joke item directly:
- **Joke Index** \(I_{\text{joke}}\):
  \[I_{\text{joke}} = (V - 300) \pmod{50}\]
- **Result**: `jokes[I_joke]` (e.g. *"Batu Bata Merah"*).

### 2.3 Pool 3: Flowers (\(350 \le V < 450\))
If \(V\) falls in this range, we select a flat Flower item directly:
- **Flower Index** \(I_{\text{flower}}\):
  \[I_{\text{flower}} = (V - 350) \pmod{100}\]
- **Result**: `flowers[I_flower]` (e.g. *"Bunga Rafflesia"*).

*Note: Adding new categories in the future simply involves increasing the hash modulus (e.g. 450 to 500) and adding a new range condition.*



## 3. CSV Database Schema
The app loads the datasets from four CSV files stored in `app/src/main/assets/`:

### 3.1 elements.csv (10 rows)
- Columns: `id,name,description,financial,romantic`
- Example elements: *Wood (Kayu)*, *Fire (Api)*, *Water (Air)*.

### 3.2 beasts.csv (30 rows)
- Columns: `id,name,description,power,mysticism,agility`
- Example beasts: *Tiger (Macan)*, *Dragon (Naga)*, *Cat (Kucing)*.

### 3.3 jokes.csv (50 rows)
- Columns: `id,name,description,financial,romantic,power,mysticism,agility`
- Example jokes: *Batu Bata Merah*, *Rice Cooker*, *Sendal Swallow*.

### 3.4 flowers.csv (100 rows)
- Columns: `id,name,description,financial,romantic,power,mysticism,agility`
- Example flowers: *Bunga Mawar*, *Bunga Melati*, *Lavender*.



## 4. Product Features & User Flow

### 4.1 Input Phase
- The user is welcomed by a dark cosmic UI.
- Inputs: Name (String) and Date of Birth.
- Validation: Name cannot be empty; Date of Birth cannot be in the future.

### 4.2 Cosmic Reveal Phase (1.5 - 2.0s)
- A cosmic loading screen simulates spiritual alignment/calculation.
- Features a pulsing glowing aura, rotating mystical compass, or starfield animation.
- Displays progress text like:
  - *"Reading spiritual coordinates..."*
  - *"Aligning planetary alignment..."*
  - *"Awakening entity..."*
- Subtle haptic feedback (vibrations) is triggered at key transition moments.

### 4.3 Revelation Screen & Locked Prophecies
- The user's Khodam is revealed with:
  - **Khodam Name**: (e.g. *Fire Kucing Oyen*, *Metal Macan Putih*)
  - **Aura Element**: (e.g. Wood, Fire, Water, Metal, Earth)
  - **Attributes**: Power, Mysticism, Agility (represented visually via clean horizontal progress bars).
  - **Prophecy/Description**: The core personality/character description is revealed immediately.
  - **Locked Prophecies**: The **Financial Prophecy** and **Romantic Prophecy** cards are initially blurred/locked with a lock icon and a button ("Unlock with Ad").
  - **Unlocking Mechanic**: Clicking "Unlock" on either card triggers a **Full-Screen Interstitial Ad**. Upon closing the ad, that specific prophecy card transitions from blurred to fully visible.
- **Social Sharing Integration**:
  - Direct sharing hooks for **WhatsApp**, **Facebook**, and **Instagram** using Android Intents.
  - Custom sharing hook for **TikTok**: Copies the formatted result and download watermark text to the system Clipboard, and deep-links to launch the TikTok application (`com.zhiliaoapp.musically`), allowing users to easily paste the text as a caption on reaction videos.
  - Shares formatted cosmic texts and promotional links (e.g. *"Check out my Khodam!"*).
- **Skip Ad Credits System**:
  - Users start with **5 Skip Ad credits** (persisted locally).
  - When an interstitial ad is triggered (either during Khodam check or when unlocking a prophecy), the user is presented with a dialog: *"Watch Ad to Proceed OR Use 1 Skip Credit (Remaining: X)"*.
  - Sharing the app to Facebook, Instagram, or WhatsApp grants the user **5 Skip Ad credits**, capped at a **maximum of 5 active credits** at any time. This prevents infinite accumulation while rewarding social engagement.

## 5. Monetization Model & Ad Fallback Waterfall

To maximize ad fill rates and guarantee revenue even during network shortages or offline usage, the app implements a 3-tier **Ad Fallback Mediation Waterfall**:

1. **Tier 1: Google AdMob & Meta Audience Network Bidding (Primary)**
   - The app initiates banner and interstitial requests via the Google Mobile Ads SDK.
   - Meta Audience Network acts as a primary real-time bidding partner inside AdMob Mediation to maximize yield.
2. **Tier 2: Direct Meta SDK / Unity Ads / AppLovin (Secondary Fallbacks)**
   - If AdMob fails to load an ad (returns `onAdFailedToLoad` error code or timeout), the `AdManager` catches the callback and immediately routes the request to direct SDK fallbacks (Meta Audience Network direct SDK or Unity Ads / AppLovin SDK).
3. **Tier 3: Thematic Mock Ads (Offline & Fail-Safe Fallback)**
   - If both network SDKs fail to load (e.g., user is offline, network latency, or "No Fill" status), the app falls back to displaying our funny **Thematic Mock Ads**. This guarantees the gameplay flow (input -> interstitial -> reveal -> unlock) is never blocked for the user while maintaining ad-readiness.


### 5.1 Ad Formats
- **Persistent Bottom Ribbon Ad**: A non-intrusive banner anchored to the bottom of the viewport, which is always active on all screens.
- **First Interstitial Ad (Check Lock)**: Spawns immediately when the user clicks "Cek Khodam". Can be bypassed by spending 1 Skip Ad credit.
- **Second Interstitial Ad (Prophecy Lock)**: Spawns when the user requests to unlock the locked *Financial Prophecy* or *Romantic Prophecy* sections. Can be bypassed by spending 1 Skip Ad credit.
- **Skip Ad Credits Utility**: Enhances user experience by giving a premium choice (ad vs. credit), while driving virality through sharing-based credit rewards.

### 5.2 Payout Optimization: AdMob & Meta Mediation Bidding
To secure maximum revenue yield, the app implements real-time bidding instead of a static waterfall list:
- **Meta Audience Network (MAN)** delivers the highest eCPM (\$2.50 – \$4.50) in SEA for lifestyle/social apps due to advanced demographic targeting, but exhibits lower fill rates (~60%).
- **Google AdMob** yields consistent eCPMs (\$1.50 – \$3.00) with a near-perfect fill rate (~98%) backed by Google's massive global advertiser base.
- **Bidding Setup**: By integrating Meta as a real-time bidding partner inside the Google AdMob Mediation panel, both networks compete in a live auction for each ad impression. This raises average yield by **20% to 40%** by automatically serving the highest-paying ad.



## 6. Revenue & Monetization Projections

### 6.1 Industry Standard Assumptions (Target Market: SEA/Indonesia)
- **Banner eCPM**: \$0.20 (refreshes every 30 seconds, average session is 2 minutes = 4 impressions/session).
- **Interstitial eCPM**: \$2.00 (shown on check submit and prophecy unlock).
- **Average Engagement**: A user performs an average of 5 checks over their lifetime (checking their own, family's, friends', or crushes' Khodam).
- **Unlock Rate**: 60% of users unlock both the Financial and Romantic prophecy cards (resulting in 2 additional interstitial impressions per check).
- **Average Ad Load per User Lifetime**: 20 banner impressions, 10 interstitial impressions (discounting skip credits used by sharing, which generates organic viral installs).
- **Estimated Lifetime Value (LTV) per User**:
  \[\text{LTV} = (20 \times \frac{\$0.20}{1000}) + (10 \times \frac{\$2.00}{1000}) = \$0.004 + \$0.02 = \$0.024\]
  *(With virality, average sharing increases the user base organically, pushing the effective LTV per paid install to **\$0.04 - \$0.05**).*

### 6.2 Projections by Milestone

| Download Milestone | Active User Base (MAU @ 25%) | Total Lifetime Impressions (Est.) | Est. Revenue Range (USD) | Est. Revenue Range (IDR) | Key Drivers |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **10k Downloads** | 2,500 | 200k Banner / 100k Interstitial | \$200 - \$500 | Rp 3.2M - Rp 8.1M | Organic social shares |
| **100k Downloads** | 25,000 | 2M Banner / 1M Interstitial | \$2,000 - \$5,000 | Rp 32M - Rp 81M | Viral TikTok trends |
| **1M Downloads** | 250,000 | 20M Banner / 10M Interstitial | \$20,000 - \$50,000 | Rp 320M - Rp 810M | Cross-promotion, localization |

## 7. Viral Growth & Marketing Strategy

### 7.1 Relatable Humor & Hyper-Local Slang
The database entries (*khodams.csv*) combine traditional Indonesian mysticism with modern internet pop culture. Using terms like *Bucin*, *Gaya Elite Dompet Sulit*, and *Menyala Abangku* makes results highly relatable and screenshot-worthy.

### 7.2 9:16 Aspect-Ratio Story-Ready Cards
The **Result Screen** is styled as a vertical card with high-fidelity glassmorphism, bright neon glowing aura frames, and a clean layout. The layout is optimized for mobile screenshots, containing a clear watermark link (*"🔮 Cek Khodam-mu di Play Store"*) to convert social views into direct downloads.

### 7.3 Suspense-Driven Video Hook (Cosmic Reveal)
The 2-second **Cosmic Reveal Phase** utilizes dynamic spinning animations and alignment text. This acts as a visual hook, encouraging users to record screen captures of their live reactions for TikTok and Instagram Reels.

### 7.4 WhatsApp/Direct Message Compatibility Check
Sharing formatting includes compatibility hooks (e.g. comparing friend and crush pairings), naturally prompting users to text friends their results directly via WhatsApp or Instagram DMs.

## 8. Visual Asset Prompt Generator

To maintain a consistent high-fidelity art style across all Khodams, all illustrations must follow the specified prompt template and style guidelines.

### 8.1 Prompt Template
For any given Khodam combining an **[Element]** and an **[Entity]**, use the following structure:
> *"A high-fidelity mystical [Element] [Entity] spirit avatar, stylized game card illustration, detailed creature mascot, dark cosmic space background with colorful stardust and nebulae, glowing neon [Color] outline/aura, epic fantasy concept art, centered square composition, game asset style."*

### 8.2 Element & Color Mapping Matrix
When generating prompts, use the following mapping for the `[Element]` visual keywords and `[Color]` aura:

| Element | Visual Keywords | Neon Aura Color |
| :--- | :--- | :--- |
| **Wood** | Ancient roots, leaves, emerald essence | Emerald Green |
| **Fire** | Solar flares, embers, dancing flames | Ruby Red or Solar Gold |
| **Water** | Flowing currents, bubbles, aqua glow | Cyan or Deep Blue |
| **Metal** | Polished steel, geometric lines, silver sheen | Platinum Silver |
| **Earth** | Crystal formations, stone shards, amber light | Amber Gold |

### 8.3 Example Prompts
1. **Wood Tiger (Macan Putih Kayu)**:
   > *"A high-fidelity mystical Wood Tiger spirit avatar, ancient roots and leaves flowing around the tiger, stylized game card illustration, detailed creature mascot, dark cosmic space background with colorful stardust and nebulae, glowing neon Emerald Green outline, epic fantasy concept art, centered square composition, game asset style."*
2. **Fire Crocodile (Buaya Api / Buaya Darat)**:
   > *"A high-fidelity mystical Fire Crocodile spirit avatar, wearing sleek futuristic sunglasses, solar flares and dancing flames around the crocodile, stylized game card illustration, detailed creature mascot, dark cosmic space background with colorful stardust and nebulae, glowing neon Ruby Red outline, epic fantasy concept art, centered square composition, game asset style."*

### 8.4 Local Generation Setup (RTX 5070 Ti 16GB & ComfyUI)
An NVIDIA RTX 5070 Ti with 16GB VRAM is exceptionally suited for local image generation. 

- **Recommended Generator**: **ComfyUI** (Node-based Stable Diffusion & Flux client).
- **Optimal Models**:
  - **Flux.1 (Dev/Schnell)**: Run the FP8 checkpoint for Flux.1 Dev or FP16 for Schnell. 16GB of VRAM allows running these state-of-the-art models with fast generation speeds (< 10 seconds per image).
  - **SDXL 1.0 (Stable Diffusion XL)**: Use a stylized/fantasy fine-tune checkpoint (e.g., *DreamShaper XL* or *Animagine XL*). Generate directly at \(1024 \times 1024\) resolution and downscale to \(512 \times 512\) for the app.
- **Workflow Setup**:
  1. Set up a standard **KSampler** node with 20-30 steps for Dev (4 steps for Schnell).
  2. Use the **Empty Latent Image** node set to \(512 \times 512\) (native Flux speed) or \(1024 \times 1024\) with a **Batch Size of 4** (to generate 4 variations simultaneously).
  3. Apply an **Upscale Image** node using `Lanczos` or a specialized anime/creature upscaler (e.g., `4x-UltraSharp`) for ultra-high-definition output before saving.
  4. **Manual Selection**: Review the 4 generated variations, manually select the single best output for that specific Khodam ID, and save/rename it as `khodam_[id].png` inside the app's drawable resources (`app/src/main/res/drawable/`).

### 8.5 Free Asset Sourcing Channels
For a solo developer, sourcing free assets keeps costs at \$0:
- **Images**:
  - **Avatars**: Local ComfyUI generation (unlimited, custom-tailored).
  - **Backgrounds**: **Pexels / Unsplash** (search for *"dark space"* or *"nebula"* for high-res royalty-free cosmic backgrounds).
  - **Icons**: **Flaticon / Google Fonts Material Symbols** (for locks, share icons, app logo).
- **Sounds & SFX**:
  - **Pixabay Audio**: Royalty-free cosmic atmospheric music and UI click sounds.
  - **Freesound.org**: Search for *"mystical chime"*, *"gong reveal"*, or *"magic spell"* under Creative Commons licenses.
- **Animations**:
  - **LottieFiles**: Free animations under Lottie format (e.g., *"cosmic spin"*, *"glowing stars"*). Lottie files are parsed dynamically in Jetpack Compose, consuming almost zero APK footprint.
  - **Animated Vector Drawables (AVD)**: Path-morphing vectors created using free tools like `shapeshifter.design`. These run directly on the Android system's RenderThread, avoiding UI thread stutters.
  - **Compose Transition APIs**: Native `animateState` or `infiniteTransition` calls inside Kotlin for rotation, scaling, and color glows.
  - **AGSL Shaders / Canvas Shaders**: Custom GPU fragment shaders (using Jetpack Compose's `RuntimeShader` or dynamic linear gradients) to animate liquid glows and iridescent holographic cards.
  - **Rive Runtime**: Interactive vector animations (`rive.app`) supporting complex state-machines with a lightweight Android runtime and a free design tier.



## 9. Advanced Interactive Features & Easter Eggs

To maximize virality and enhance user retention, the app incorporates premium gameplay and easter egg mechanisms:

### 9.1 Aura Fingerprint Scanner UI
- On the input screen, instead of a simple submit button, the user holds their finger on a glowing, animated "Aura Scanner" pad.
- A 2-second hold triggers sequential haptic vibrations that accelerate, simulating an energetic scan.

### 9.2 Easter Egg Overrides
Before running the hash algorithm, the engine checks a predefined list of string matches (case-insensitive) for famous names:
- `"Windah Basudara"` -> Overrides to **"Raja Bocil Kematian"** (Power: 99, Agility: 99, Mysticism: 90).
- `"Gibran"` -> Overrides to **"Samsul Reborn"** (Power: 85, Agility: 90, Mysticism: 95).
- `"Jokowi"` -> Overrides to **"Bapak Pembangunan"** (Power: 95, Agility: 90, Mysticism: 90).

### 9.3 Holographic Premium Card Designs
- Unlocking both the **Financial** and **Romantic** prophecies for any Khodam automatically upgrades the result card.
- The upgraded card features a moving iridescent holographic sheen effect (simulated via Jetpack Compose shaders/gradients), encouraging screenshots.






