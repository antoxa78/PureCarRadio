# Changelog

All notable changes to this project will be documented in this file.

## [1.4.1] - 2026-06-14

### Fixed
- **Android Auto/Automotive Connection**: Resolved service binding issues and added required Automotive metadata to the manifest.
- **Media Content Browsing**: Enhanced `PlaybackService` with full content hierarchy support and search functionality for car head units.
- **Project Integrity**: Repaired source code corruption by removing remaining merge conflict markers across the project.
- **Mirror Fallback**: Fixed logic in `RadioRepository` to correctly handle mirror outages and metadata recovery.

## [1.4.0] - 2026-06-14

### Added
- **Full Localization**: All UI strings moved to `strings.xml` — ready for proper translation into Russian, Ukrainian, and Hebrew.
- **Debug Build Variant**: New `debug` build type with `.debug` application ID suffix, enabling simultaneous installation of debug and release builds on the same device.

### Changed
- **Version bump**: `versionCode` 7 → 8, `versionName` 1.3.2 → 1.4.0.
- **Screensaver mode labels**: "Deep Black" is now labeled "Deep Black (OLED Safe)" in the TV settings screen for clarity.

### Fixed
- **Duplicate constant**: `CMD_PLAY_STATION` was declared twice (top-level and in `companion object`). Removed the top-level duplicate; the canonical reference is now `PlaybackService.CMD_PLAY_STATION`.
- **Release signing**: Removed `signingConfig = signingConfigs.getByName("debug")` from the `release` build type. Release APKs must now be signed with a proper release keystore (see build instructions below).

### Build Notes
To produce a signed release APK, create a keystore and add to `build.gradle.kts`:
```
signingConfigs {
    create("release") {
        storeFile = file("your-key.jks")
        storePassword = System.getenv("KEYSTORE_PASSWORD")
        keyAlias = System.getenv("KEY_ALIAS")
        keyPassword = System.getenv("KEY_PASSWORD")
    }
}
```
Then set `signingConfig = signingConfigs.getByName("release")` in the `release` block.

---

## [1.2.0] - 2025-01-31

### Added
- **Popular Stations**: New dedicated menu section for trending stations based on community votes.
- **Audio Passthrough (Hi-Res)**: Experimental mode to bypass Android's 48kHz resampler, featuring floating-point PCM output and renderer optimization (specifically for Nvidia Shield).
- **Anti-Burn-In Screensaver**: The "Station Info" screensaver now bounces across the screen to protect OLED/Plasma panels.
- **Live Stats in Screensaver**: Added real-time playback duration and an intelligent waveform that stops when audio is paused.
- **Vote Counts**: Station cards now display the number of community votes.

### Changed
- **Now Playing Bar**: Relocated playback timer to the center controls and improved layout for wider country names.
- **Drawer Layout**: Navigation drawer now uses a scrollable `LazyColumn` to prevent overlapping on smaller screens or long lists.
- **Error Handling**: Implemented automatic "Skip to Next" when a station URL is unplayable.

### Fixed
- Fixed critical layout issues where the navigation drawer would distort screen titles.
- Fixed missing country icons in Favourites and Recent lists by adding a background metadata refresh.
- Standardized all application icons and banners to fix legacy "Old Icon" display issues on newer Google TV boxes.

## [1.1.0] - 2025-01-30
- Added country flags and bitrate info near station names.
- Replaced VU meters with a modern center-weighted Waveform Analyzer.
- Added adaptive icon support.

## [1.0.0] - 2025-01-20
- Initial release with basic station browsing, playback, and search.
