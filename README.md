# Pure Radio TV 📻

Pure Radio TV is a premium, retro-styled internet radio application designed specifically for Android TV and Google TV (Nvidia Shield, Chromecast, Sony/TCL TVs, etc.). It leverages the vast [Radio Browser](https://www.radio-browser.info/) database to provide thousands of stations with a focus on high-fidelity audio and a classic aesthetic.

## ✨ Features

- **📺 Optimized for TV**: Fully navigable with a standard D-pad remote. High-contrast UI for comfortable viewing from the couch.
- **🎙️ Global Discovery**: Access thousands of stations via the community-driven Radio Browser database.
- **🔥 Popular Stations**: Dedicated section for the world's most-voted and trending radio stations.
- **🔊 Bit-Perfect Audio**: Experimental "Audio Passthrough" mode designed for Nvidia Shield to bypass the Android system resampler and output high-fidelity PCM.
- **🏷️ Smart Browsing**: Explore by Genres (with personalization), Countries, or use the integrated Search.
- **📊 Real-time Visuals**: Includes a center-weighted animated Waveform Analyzer that reacts to the music.
- **🚗 Android Car Support**: Fully compatible with Android Auto and Android Automotive OS. Control playback and browse stations directly from your car's head unit.
- **🖼️ Anti-Burn-In Screensaver**: Dynamic "Bouncing" screensaver with live playback stats and waveform, specifically designed to protect OLED/Plasma TV screens.
- **⭐ Favorites & Recents**: Manage your favorite stations and quickly return to recently played ones with automatic metadata "healing".
- **🛡️ Native TV Look**: High-definition Adaptive Icons and Leanback Banners for a premium look on the Google TV home screen.

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose for TV (Material 3)
- **Audio Engine**: Android Media3 (ExoPlayer)
- **Network**: Retrofit & Gson
- **Image Loading**: Coil

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug or newer.
- Android SDK 30+ (Designed for Android TV).

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/[your-username]/PureRadio.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and run on an Android TV emulator or physical device.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Data provided by the community-driven [Radio-Browser.info](https://www.radio-browser.info/).
- Flag icons provided by [Flagpedia.net](https://flagpedia.net/).
