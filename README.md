**Crescendo**
-----------------

[![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-blue.svg?logo=kotlin)](http://kotlinlang.org)

## **Developer**
[Paranid5](https://github.com/dinaraparanid)

## **About App**
**Crescendo** is a music player Android app that **plays audio from the YouTube (including livestreams)!**
Additionally, it supports caching of videos to multiple audio and video formats.
Application allows to play both networked audio and local files with multiple audio effects.
Crescendo provides multiple features to move your music experience to a new level!

### **Preview**

**Playback of local tracks**

<p>
    <img src="https://i.ibb.co/JkdSjn2/playing-track.jpg" alt="playing_preview" width="100">
    &nbsp;
    <img src="https://i.ibb.co/KhGH5qt/tracks.jpg" alt="tracks" width="100">
    &nbsp;
    <img src="https://i.ibb.co/PwMtJqm/track-sort.jpg" alt="track_sort" width="100">
</p>

**Support of audio effects (Equalizer, Bass, Reverb, Pitch and Speed shifter)**

<img src="https://i.ibb.co/TRN3zF5/audio-effects.jpg" alt="audio_effects" width="100">

**Customize your current playlist**

<img src="https://i.ibb.co/cQyxngv/current-playlist.jpg" alt="cur_playlist" width="100">

**Stream tracks and live streams from the YouTube**

<p>
    <img src="https://i.ibb.co/vL6GqhD/fetch-youtube.jpg" alt="fetch_youtube" width="100">
    &nbsp;
    <img src="https://i.ibb.co/bBFTvP5/playing-youtube.jpg" alt="playing_youtube" width="100">
    &nbsp;
    <img src="https://i.ibb.co/7k4MV6s/livestreaming.jpg" alt="live_streaming" width="100">
</p>

**Cache video to the preferred format**

<img src="https://i.ibb.co/WK042D4/cache-youtube.jpg" alt="cache_video" width="100">

**Control playback and cashing with notifications**

<img src="https://i.ibb.co/zfkTCTn/notifications.jpg" alt="notifications" width="100">

**Landscape mode support**

<img src="https://i.ibb.co/r4xyJq9/landscape.jpg" alt="landscape" width="100">

## **Stack**

<ul>
    <li>General</li>
    <ul>
        <li>Kotlin 1.9</li>
        <li>Coroutines</li>
        <li>Koin</li>
        <li>Ktor (Content Negotiation + Json, Logging extensions)</li>
        <li>KotlinX.Serialization</li>
        <li>Arrow</li>
    </ul>
    <p></p>
    <li>Media</li>
    <ul>
        <li>Exoplayer (as part of AndroidX.Media3)</li>
        <li>FFMpeg</li>
        <li>JAudioTagger</li>
        <li>Java Audio Video Encoder</li>
        <li>AndroidX.Media (audio effects)</li>
        <li>Android Storage Access Framework</li>
        <li>Android Media Store</li>
        <li>Android Media Scanner</li>
    </ul>
    <p></p>
    <li>UI</li>
    <ul>
        <li>Compose Material</li>
        <li>Coil + Compose extensions</li>
        <li>AndroidX.Palette</li>
        <li>AndroidX.Navigation.Compose (NavHost)</li>
        <li>AndroidX.ConstraintLayout (Compose version)</li>
        <li>Composition Local</li>
        <li>Android Canvas (Compose version)</li>
    </ul>
    <p></p>
    <li>Android specific</li>
    <ul>
        <li>View Model (+ Kotlin extensions)</li>
        <li>Foreground Services + Broadcast Receivers</li>
        <li>DataStore</li>
        <li>KotlinX.Parcelize</li>
    </ul>
</ul>

## **Current Status:**

**Alpha V 0.2.0.0**

### **Implemented features:**
1. Media playback of YouTube videos and live streams
2. Media playback of local tracks
3. Audio and Video caching to multiple formats
4. Current playlist system (add and remove tracks)
5. Audio Effects: EQ, Bass Boost, Reverb, Pitch and Speed shifters
6. Updates notifications

### **TODO:**
1. Track trimming
2. Recently listened videos/livestreams
3. Artists, albums and custom playlists
4. Favourites
5. Tags changing
6. Lyrics
7. App customization and settings

## **System Requirements**
**Android 5.0** or higher

Stable internet connection to play audio from YouTube and cash video is required

## **License**
*GNU Public License V 3.0*
