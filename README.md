# Pure Car Radio 📻

Pure Car Radio is a premium, retro-styled internet radio application for Android Auto and Android Automotive OS, also usable on Android phones. It leverages the vast [Radio Browser](https://www.radio-browser.info/) database to provide thousands of stations with a focus on high-fidelity audio and a classic aesthetic.

## ✨ Features

- **🚗 Built for the Car**: Full Android Auto / Android Automotive OS support with native browsing, search, and playback controls directly on the car head unit.
- **🎙️ Global Discovery**: Access thousands of stations via the community-driven Radio Browser database.
- **🔥 Popular Stations**: Dedicated section for the world's most-voted and trending radio stations.
- **🎵 Now Playing Metadata**: Real-time song and artist titles via ICY metadata, with bitrate and codec info on the Now Playing bar.
- **🔊 Bit-Perfect Audio**: Experimental "Audio Passthrough" mode that bypasses the Android system resampler to output high-fidelity PCM.
- **🏷️ Smart Browsing**: Explore by Genres (with personalization), Countries, or use the integrated Search.
- **📊 Real-time Visuals**: Includes a center-weighted animated Waveform Analyzer that reacts to the music.
- **🖼️ Screensaver Mode**: "Station Info" screensaver with live playback stats and waveform to protect car and phone displays from burn-in.
- **⭐ Favorites & Recents**: Manage your favorite stations and quickly return to recently played ones with automatic metadata "healing".

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Audio Engine**: Android Media3 (ExoPlayer)
- **Network**: Retrofit & Gson
- **Image Loading**: Coil

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug or newer.
- Android SDK 29+.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/antoxa78/PureCarRadio.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and run on an automotive emulator, Android Auto Desktop Head Unit, or a physical phone/device.

## 📦 Releases

Prebuilt APKs are attached to the [GitHub Releases](https://github.com/antoxa78/PureCarRadio/releases) page.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Data provided by the community-driven [Radio-Browser.info](https://www.radio-browser.info/).
- Flag icons provided by [Flagpedia.net](https://flagpedia.net/).
