# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

## [1.8.2] - 2026-10-15

### Changed
- **Version bump**: `versionCode` 35 → 36, `versionName` 1.8.1 → 1.8.2.

### Fixed
- **Now Playing after rotating**: Rotating the phone while Now Playing was open left the full-screen player window at its old size, so Previous, Play/Pause and Next stopped responding in landscape. The player now rebuilds itself for the new orientation.

## [1.8.1] - 2026-10-15

### Changed
- **Version bump**: `versionCode` 34 → 35, `versionName` 1.8.0 → 1.8.1.
- **Bitrate filters (phone / Android Automotive UI)**:
  - The filter chips are now shown on every screen they apply to: Popular and opened Genres/Countries lists got them (previously the saved filter was applied there with no visible chips).
  - Clearer labels: "< 192k", "192k+" and "Lossless" instead of "Low", "High" and "FLAC" (translated for Russian, Ukrainian and Hebrew; also used on TV).
  - Each chip shows how many loaded stations it matches (e.g. "192k+ · 41"), and a clear (✕) chip appears while any filter is on.
  - The filter bar always fits the screen: on Search it has its own row below Name/Tag, the chips are more compact (filter icon instead of the "Filter:" caption, no check marks), and the chips wrap onto a second line on narrow screens or with long translations.
  - List headers show "34 of 100 stations" while a filter hides some stations, and a list emptied by the filter shows "No stations match the quality filter" with a "Clear filters" button.
  - One shared filter bar for Home, Popular, Genres, Countries and Search; removed an unused TV filter component.

### Fixed
- **Notification tap**: Tapping the playback notification or the system media controls now opens Pure Radio. The media session had no app screen set, so some phones opened another media app (e.g. YouTube) instead.
- **Previous / Next**: The buttons now work when the player holds a single station, e.g. after "Resume last station" started playback (common on car / landscape displays). Previously they only restarted the same station; now they step through the list on screen, or favourites, or recent stations — whichever contains the playing station.
- **Filter taps**: The list updates instantly from the loaded stations and reloads once after you stop tapping (previously every tap started its own request). A newer load now cancels the previous one, so a slower old response can no longer replace the newer list, and leaving a list cancels its pending loads so late results don't land on another screen. Switching away from Home also stops its top-stations load, which could previously replace the next tab's list.
- **Home filters**: Changing the bitrate filters (or "Hide broken stations") while a genre/country opened from a Home tile is showing now updates its station list. Previously only the Home tiles were reloaded and the open list kept the old filter. Tile counts are also refreshed when going back to the tiles.

## [1.8.0] - 2026-10-14

### Added
- **Check for updates**: New "About → Check for updates" item in Settings (phone/Automotive and TV). It queries the latest GitHub release, compares it with the installed version and, if newer, shows the release notes with a Download button (opens the APK link in the browser; on devices without a browser the link is shown instead).
- **Automatic update check**: On app start (at most once a day, a few seconds after launch) the app checks GitHub in the background and shows the update dialog only when a newer version exists. "Later" skips that version until an even newer one is released; failures are silent. Can be turned off with Settings → About → "Check for updates automatically" (on by default). The dialog never appears over the screensaver.

### Changed
- **Faster Home genres on slow connections**:
  - Home tiles appear immediately (last known counts are saved on the device) and each tile updates as soon as its own data arrives instead of waiting for every genre.
  - Home tiles now use a single tag/country request per page instead of tag + name search; loaded tiles are cached in memory for 15 minutes so returning to Home doesn't re-download.
  - One shared HTTP client (connection pool) for all Radio Browser mirrors and for the car media browser; the last working mirror is remembered and tried first.
  - Successful empty results are no longer retried on every mirror (previously 5 round trips per empty page); the tag + name searches for station lists run in parallel.

## [1.7.4] - 2026-10-13

### Changed
- **Version bump**: `versionCode` 32 → 33, `versionName` 1.7.3 → 1.7.4.
- **Visual Refresh (phone / Android Automotive UI)**:
  - Complete colour schemes for all six themes — every Material 3 role (surface containers, outlines, secondary text, chips, dialogs, menus) is now defined, so the stock purple-grey defaults no longer leak into the UI. Blue Neon now uses the brand navy from the app icon and splash screen instead of warm brown-black.
  - Montserrat (OFL) headings to match the "Pure Radio" wordmark; body text stays on the system font.
  - Redesigned station cards (larger artwork, flag, favourite badge, animated now-playing equalizer, votes and codec/bitrate), photo tiles for genres/tags, and flag tiles for countries.
  - Consistent screen headers with back buttons and station counts; pill search fields; scrollable filter chips with check marks.
  - Settings grouped into rounded sections with icon badges, radio buttons for choices, and theme previews (swatches) in the theme picker.
  - Now Playing: ambient blurred-artwork backdrop (Android 12+), glowing artwork card, LIVE / codec pills, rounded progress bar and larger transport controls; the landscape (car display) layout gets a full-height artwork.
  - Now-playing bar, loading overlay, empty states and the navigation menu (current section highlighted) restyled.
- **TV theme**: TV screens now follow the selected app theme (previously they always used the default TV palette).

### Fixed
- **Errors on phone**: Error messages are now shown as a dismissible banner instead of being silently cleared.
- **Back navigation**: System back from a genre/country station list returns to the list instead of leaving the section.

## [1.7.3] - 2026-09-08

### Changed
- **Version bump**: `versionCode` 31 → 32, `versionName` 1.7.2 → 1.7.3.

### Added
- **Genre Artwork Diversification**: Unmapped Radio Browser genre tags (and deep house, psytrance, ska, 80s) now spread across a small pool of generic mood photos instead of all collapsing onto one image, keeping the car display's genre grid varied.
- **Hide Broken In Car Browsing**: The "hide broken stations" preference now applies to the Android Auto / Automotive media browser (popular, genres, countries, search) so car displays match the phone UI.

### Fixed
- **Broadcast Receiver Security**: The stop-radio receiver is now registered as not-exported on every API level (via the AndroidX compat shim below API 33), preventing other apps on the device from broadcasting `ACTION_STOP_RADIO` and force-stopping playback.
- **Home Genre Defaults Persistence**: A deliberately emptied home genre selection no longer keeps reverting to the default genre set on the car media browser.

## [1.7.2] - 2026-09-04

### Changed
- **Version bump**: `versionCode` 30 → 31, `versionName` 1.7.1 → 1.7.2.

### Fixed
- **Car Category Artwork**: Replaced 16 dead Unsplash image URLs (HTTP 404) in the genre/category artwork mapping — including pop/hits/top/chart, rock variants, jazz/blues/latin, classical/orchestra/opera, electronic genres, 60s/oldies/retro, and the Home Screen tile. Categories like "Hits" now display their pictures again on Android Auto / Automotive screens. All 48 image URLs verified live and visually checked for theme fit.

## [1.7.1] - 2026-08-29

### Changed
- **Version bump**: `versionCode` 29 → 30, `versionName` 1.7.0 → 1.7.1.

### Fixed
- **Car Home Screen Genres**: Home-screen genre/country titles are now properly capitalized on Android Auto / Automotive displays.
- **Car Genre Artwork**: Browsable genre/country items now expose both `DISPLAY_ICON_URI` and `ALBUM_ART_URI` so more car head units render their artwork.
- **Last Station UUID**: `PlaybackService` now persists the real station UUID instead of the contextual `parentId|station:uuid` media ID, keeping the phone UI in sync after car/background playback changes.
- **Custom Command Station Tracking**: `CMD_PLAY_STATION` now updates `last_station_uuid` as well as `last_station_json`.
- **Safe Browse IDs**: Genre and country names are URL-encoded inside MediaItem IDs, preventing issues with spaces or special characters on car displays.
- **Search Country Dialog**: Long-pressing a country tag in search results now shows the correct add/remove-country dialog.
- **Localized Root Title**: The media library root title now uses `R.string.app_name` for proper localization.

## [1.6.9] - 2026-08-19

### Changed
- **Version bump**: `versionCode` 27 → 28, `versionName` 1.6.8 → 1.6.9.

### Fixed
- **Car Display App Icon Fallback**: Stations without a favicon now show the default app icon on the app's own fullscreen car display UI (Now Playing bar, screensaver, station cards, and PiP) instead of falling back to the country flag.

## [1.6.8] - 2026-08-17

### Changed
- **Version bump**: `versionCode` 26 → 27, `versionName` 1.6.7 → 1.6.8.

### Fixed
- **Car Display Default App Icon**: Stations without a favicon now show the default app icon on the fullscreen Now Playing view of the car display. The icon is embedded as artwork data in the media session instead of an `android.resource://` URI, which car UIs (Android Auto / Automotive) cannot resolve. Verified on an Android Automotive emulator.

## [1.6.7] - 2026-08-14

### Changed
- **Version bump**: `versionCode` 25 → 26, `versionName` 1.6.6 → 1.6.7.
- **Phone UI Polish**: Phone grids now adapt to wider screens, browse cards use consistent rounded elevation, and the top-bar menu spacing is more compact.
- **Theme Contrast**: Improved selected-container text contrast in the Blue Neon and Forest themes.

### Fixed
- **Car Display Artwork Fallback**: Stations without a favicon now use a local application artwork URI that Android Auto and Android Automotive OS can resolve.

## [1.6.6] - 2026-08-14

### Changed
- **Version bump**: `versionCode` 24 → 25, `versionName` 1.6.5 → 1.6.6.

### Fixed
- **Station Artwork Fallback**: Stations with missing or invalid artwork now consistently show the app icon on phone and car displays.
- **Artwork URL Validation**: Invalid, unsupported, blank, and `.ico` artwork URLs now fall back to a country flag or the app icon.
- **Playback Retry Safety**: Delayed stream retries are cancelled when playback stops, the station changes, or the service is destroyed.
- **Playback Lifecycle Cleanup**: Controller connections and stopped-playback metadata are cleaned up correctly.

## [1.6.5] - 2026-08-13

### Changed
- **Version bump**: `versionCode` 23 → 24, `versionName` 1.6.4 → 1.6.5.

### Fixed
- **Car Display Scroll Reset**: Browsing lists on the car display no longer snap back to the top while audio is playing. Periodic position updates are disabled on the media session, which Android Auto/Automotive treated as list refreshes.
- **Car Display Default Icon**: Stations without artwork (no favicon or country flag) now show the app icon on the car display. The icon is delivered as artwork data since car UIs cannot resolve the app's `android.resource://` URIs.
- **Favourites Vote Counts**: Stations imported from playlists now resolve real vote counts from the Radio Browser database (matched by stream URL first, then exact name) instead of showing 0.
- **Now Playing on Low-Resolution Landscape Displays**: Player controls are now always visible via a compact side-by-side layout, and the default station icon renders at a sharper size.

## [1.6.4] - 2026-08-12

### Changed
- **Version bump**: `versionCode` 22 → 23, `versionName` 1.6.3 → 1.6.4.

### Fixed
- **Car Display "Source error"**: Browsing from the car display no longer shows source error messages when a radio-browser mirror is slow or unreachable. Browse/search failures now return empty results, network calls are bounded by 10s timeouts, `onGetItem` resolves stations locally first, and the repository uses fast OkHttp timeouts.

## [1.6.3] - 2026-08-12

### Changed
- **Version bump**: `versionCode` 21 → 22, `versionName` 1.6.2 → 1.6.3.

### Fixed
- **Car Display Station Name**: Station name now shows on the car display (Android Auto) as the title fallback before ICY track metadata arrives or for streams without ICY metadata.

## [1.6.2] - 2026-08-12

### Changed
- **Version bump**: `versionCode` 20 → 21, `versionName` 1.6.1 → 1.6.2.

### Fixed
- **Stale Station Info**: Previous station metadata no longer shown when switching to another station.
- **Stream Titles (ICY)**: Stream song titles now display on the now playing screen. Playback media items no longer mask in-stream ICY metadata with the station name.
- **Stream Titles (HTTP)**: ICY titles for streams such as `http://dorognoe.hostingradio.ru:8000/radio` now display. The service broadcasts raw ICY metadata directly to the app, bypassing merged metadata that could drop or mask the dynamic title.

## [1.6.1] - 2026-08-12

### Fixed
- **Metadata Display**: Fixed an issue where stream artist and song title were not appearing on the phone screen.
- **Sync Logic**: Improved `MediaController` synchronization for immediate metadata display on connection.
- **UI Robustness**: Added support for `displayTitle` metadata field as a fallback for various stream formats.

## [1.6.0] - 2026-08-12

### Added
- **Dynamic Metadata**: Real-time artist and song title updates on phone, TV, and car screens via ICY metadata.
- **Stream Info**: Bitrate and codec information displayed on car head units and the phone's "Now Playing" bar.
- **Enhanced Car Experience**: Automatic injection of stream quality metrics into car display metadata.

### Fixed
- **UI Consistency**: Standardized bitrate indicators across all device form factors.

## [1.5.2] - 2026-07-18

### Added
- **New Theme**: Added "Vibrant Blue" as a dedicated theme option, featuring the modern blue design.

### Changed
- **Version bump**: `versionCode` 15 → 16, `versionName` 1.5.1 → 1.5.2.

### Fixed
- **Theme Polish**: Fixed "Retro Gold" theme name and colors (now features actual gold and wood design).
- **UI Localization**: Settings menu now displays localized theme names (e.g., "Retro Gold") instead of technical enum names.

## [1.5.1] - 2026-07-17

### Added
- **Enhanced Splash Screen**: New custom splash screen with radial gradients, a pulsating logo glow, and a retro grain texture for a more premium experience.
- **Universal Branding Update**: New high-contrast application icon and placeholder assets for a unified "Pure Radio" design.
- **Native Playlist Support**: Full integration with Media3 native playlists. Skip (Next/Previous) now works flawlessly on car displays, Bluetooth speakers, and notification controls.
- **Enhanced Car Library**: Expanded car display menu to include "Recent", "Countries", and a mirrored "Home Screen" category for personalized browsing.
- **Native Car Search**: Enabled native system search support for Android Auto and Automotive head units.
- **Polished UI**: Implementation of floating rounded "Now Playing" bars and improved typography for better readability.

### Fixed
- **Selection Bug**: Resolved an issue where stations could not be selected from certain UI categories due to missing flags.
- **Contextual Playlists**: Selecting a station from a car's browser now automatically loads the rest of that category into the background playlist.
- **Service Stability**: Moved transition and error handling logic to the background service for consistent control even when the phone UI is closed.
- **Icon Alignment**: Fixed centering issues for adaptive launcher icons and TV banners.

## [1.5.0] - 2026-06-25

### Added
- **Stream Resilience**: Custom ExoPlayer `LoadControl` with 30-60s buffer for radio streaming stability
- **Auto-Reconnect**: Retries same station 3x with backoff (2s, 4s, 6s) before falling back to next station
- **HTTP Timeouts**: 15s connect / 30s read timeouts on data source for faster failure detection
- **New App Icon**: Replaced vector icon with custom PNG launcher icon across all densities

### Fixed
- **Car Display Connection**: Retry `MediaController` binding up to 3x (fixes "connection error" on slow systems)
- **Car Display Selection**: Station cache eliminates redundant API calls in `onGetItem` (fixes "unable to select")
- **HLS Detection**: Properly detects `.m3u`/`.m3u8` extensions and codec substrings (fixes silent playback failures)
- **Crash**: Duplicate `FocusRequester` in `StationGrid` removed
- **Crash**: `ConcurrentModificationException` in `stationCache`/`faviconCache` — switched to `ConcurrentHashMap`
- **ANR**: Removed `runBlocking` network call on main thread during favicon fetch
- **Memory Leak**: Capped station cache at 500 entries with LRU eviction
- **Memory Leak**: Old `MediaController` futures cancelled on retry/reconnection
- **Export**: M3U files now use UTF-8 encoding (fixes corruption with non-Latin station names)
- **Race Condition**: `loadMoreStations` guard moved inside coroutine
- **UX**: Back from drawer closes it instead of quitting app
- **UX**: Error banner replaces full-screen error when content is available
- **Phone**: Long-press favorite toggle works on non-TV devices
- **File Picker**: Export "save here" uses correct filename path
- **UI**: Sort buttons use localized strings
- **UI**: `getGenreColor` floor raised to 80 (no near-black colors)
- **UI**: Empty genre grid shows "No genres found" message

## [1.4.2] - 2026-06-14

### Fixed
- **UI Responsiveness**: Improved focus handling on TV screens when navigating back from deep-dive categories.
- **Resource Management**: Optimized favicon caching logic to prevent potential memory spikes during long browsing sessions.
- **Auto-Update Logic**: Corrected a minor issue where the database update interval could reset after a manual update.

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
