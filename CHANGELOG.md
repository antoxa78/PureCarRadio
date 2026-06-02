# Changelog

All notable changes to this project will be documented in this file.

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
