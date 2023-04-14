package com.paranid5.mediastreamer.presentation

import java.io.Serializable

sealed class Screens(val title: String) : Serializable {
    companion object {
        private const val serialVersionUID = 6162363934932631034L
    }

    sealed class StreamScreen(title: String) : Screens("stream/$title") {
        companion object {
            private const val serialVersionUID = -9190403684670003934L
        }

        object Searching : StreamScreen("searching") {
            private const val serialVersionUID = 5819957036047179941L
        }

        object Streaming : StreamScreen("streaming") {
            private const val serialVersionUID = -1606073483225906713L
        }
    }

    object AboutApp : Screens("about_app") {
        private const val serialVersionUID = 8225338587721480497L
    }

    object Favourite : Screens(title = "favourites") {
        private const val serialVersionUID = -3030101473142004909L
    }

    object Settings : Screens(title = "settings") {
        private const val serialVersionUID = -8106771731336221725L
    }
}