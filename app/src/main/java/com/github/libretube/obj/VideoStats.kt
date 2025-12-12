package dev.jch0029987.libretibs.obj

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoStats(
    val videoId: String,
    var videoInfo: String,
    var videoQuality: String,
    var audioInfo: String
) : Parcelable
