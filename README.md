**Crescendo**
-----------------

[![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-blue.svg?logo=kotlin)](http://kotlinlang.org)

## **Developer**
[Paranid5](https://github.com/dinaraparanid)

## **About App**
**Crescendo** is a music player Android app that
**plays both local files and audio from YouTube.**
Application provides multiple features to move
your music experience to a new level!

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

**Stream tracks from the YouTube**

<p>
    <img src="https://i.ibb.co/vL6GqhD/fetch-youtube.jpg" alt="fetch_youtube" width="100">
    &nbsp;
    <img src="https://i.ibb.co/bBFTvP5/playing-youtube.jpg" alt="playing_youtube" width="100">
</p>

**Cash video to the preferred format**

<img src="https://i.ibb.co/jk1PB6m/cash-youtube.jpg" alt="cash_video" width="100">

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

**Alpha V 0.1.1.0**

### **Implemented features:**
1. Media playback with local tracks and youtube videos
2. Audio and Video cashing to multiple formats
3. Current playlist system (add and remove tracks)
4. Audio Effects: EQ, Bass Boost, Reverb, Pitch and Speed shifters
5. Updates notifications

### **TODO:**
1. Artists, albums and playlists
2. Favourites
3. Track trimming
4. Tags changing
5. Lyrics
6. App customization and settings

## **System Requirements**
**Android 5.0** or higher

Stable internet connection to play audio and cash videos is required

## **License**
*GNU Public License V 3.0*
