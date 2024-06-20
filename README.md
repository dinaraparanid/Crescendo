**Crescendo**
-----------------

[![Kotlin](https://img.shields.io/badge/kotlin-2.0.0-blue.svg?logo=kotlin)](http://kotlinlang.org)

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
    <img src="https://i.ibb.co/jk3m0wW/image.png" alt="playing_preview" width="100">
    &nbsp;
    <img src="https://i.ibb.co/TqBY8SD/image.png" alt="tracks" width="100">
    &nbsp;
    <img src="https://i.ibb.co/HrfWkdV/image.png" alt="track_sort" width="100">
</p>

**Support of audio effects (Equalizer, Bass, Reverb, Pitch and Speed shifter)**

<p>
    <img src="https://i.ibb.co/85jzbx4/image.png" alt="audio_effects" width="100">
    &nbsp;
    <img src="https://i.ibb.co/wy68hdZ/image.png" alt="audio_effects" width="250">
</p>

**Customize your current playlist**

<img src="https://i.ibb.co/ZcXdSPV/image.png" alt="cur_playlist" width="100">

**Stream tracks and live streams from the YouTube, cache your favourites**

<p>
    <img src="https://i.ibb.co/G9qHPvS/image.png" alt="fetch_youtube" width="100">
    &nbsp;
    <img src="https://i.ibb.co/kxzHbfF/image.png" alt="playing_youtube" width="100">
    &nbsp;
    <img src="https://i.ibb.co/Pm3418Z/image.png" alt="live_streaming" width="100">
</p>

**Control playback and caching with notifications**

<img src="https://i.ibb.co/QQJZ82b/image.png" alt="notifications" width="100">

**Trim tracks and convert  to different audio formats**

<p>
    <img src="https://i.ibb.co/xMZtnK2/image.png" alt="landscape" width="100">
    &nbsp;
    <img src="https://i.ibb.co/rxmsKCW/image.png" alt="landscape" width="100">
    &nbsp;
    <img src="https://i.ibb.co/6BkSwcc/image.png" alt="landscape" width="250">
</p>

## **Stack**

<ul>
    <li>General</li>
    <ul>
        <li>Kotlin 2.0</li>
        <li>Coroutines + Flow</li>
        <li>Koin</li>
        <li>Ktor + OkHttp (Content Negotiation + Json)</li>
        <li>KotlinX.Serialization</li>
        <li>SQLDelight</li>
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

**Beta V 0.4.0.0**

### **Implemented features:**
1. Media playback of YouTube videos and live streams
2. Media playback of local tracks
3. Audio and Video caching to multiple formats
4. Current playlist system (add and remove tracks)
5. Audio Effects: EQ, Bass Boost, Reverb, Pitch and Speed shifters
6. Audio tracks trimming
7. Updates notifications

### **TODO:**
1. Recently listened videos/livestreams
2. Tags changing
3. Artists, albums and custom playlists
4. Favourites
5. Lyrics
6. App customization and settings

## **System Requirements**
**Android 5.0** or higher

Stable internet connection to play audio from YouTube and cache video is required

## **License**
*GNU Public License V 3.0*
