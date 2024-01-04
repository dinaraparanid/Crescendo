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
    <img src="https://i.ibb.co/XVYpVkT/image.png" alt="tracks" width="100">
    &nbsp;
    <img src="https://i.ibb.co/2qhMsYs/image.png" alt="track_sort" width="100">
</p>

**Support of audio effects (Equalizer, Bass, Reverb, Pitch and Speed shifter)**

<p>
    <img src="https://i.ibb.co/DLzWBzx/image.png" alt="audio_effects" width="100">
    &nbsp;
    <img src="https://i.ibb.co/GxMZRCk/image.png" alt="audio_effects" width="250">
</p>

**Customize your current playlist**

<img src="https://i.ibb.co/jWtyPCD/image.png" alt="cur_playlist" width="100">

**Stream tracks and live streams from the YouTube, cache your favourites**

<p>
    <img src="https://i.ibb.co/fGvZ4MC/image.png" alt="fetch_youtube" width="100">
    &nbsp;
    <img src="https://i.ibb.co/w7xF658/image.png" alt="playing_youtube" width="100">
    &nbsp;
    <img src="https://i.ibb.co/7k4MV6s/livestreaming.jpg" alt="live_streaming" width="100">
</p>

**Control playback and caching with notifications**

<img src="https://i.ibb.co/k2VjrjF/image.png" alt="notifications" width="100">

**Trim tracks and convert  to different audio formats**

<p>
    <img src="https://i.ibb.co/j8KSDZM/image.png" alt="landscape" width="100">
    &nbsp;
    <img src="https://i.ibb.co/T4fzgF7/image.png" alt="landscape" width="100">
    &nbsp;
    <img src="https://i.ibb.co/LYHg5Xr/image.png" alt="landscape" width="250">
</p>

## **Stack**

<ul>
    <li>General</li>
    <ul>
        <li>Kotlin 1.9</li>
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

**Alpha V 0.3.0.0**

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
2. Artists, albums and custom playlists
3. Favourites
4. Tags changing
5. Lyrics
6. App customization and settings

## **System Requirements**
**Android 5.0** or higher

Stable internet connection to play audio from YouTube and cache video is required

## **License**
*GNU Public License V 3.0*
