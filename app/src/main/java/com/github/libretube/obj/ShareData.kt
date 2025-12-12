package dev.jch0029987.libretibs.obj

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShareData(
    val currentChannel: String? = null,
    val currentVideo: String? = null,
    val currentPlaylist: String? = null,
    var currentPosition: Long? = null
) : Parcelable
