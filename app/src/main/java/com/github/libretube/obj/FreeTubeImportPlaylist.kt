package dev.jch0029987.libretibs.obj

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FreeTubeImportPlaylist(
    @SerialName("playlistName") val name: String = "",
    var videos: List<FreeTubeVideo> = listOf(),
    var protected: Boolean = true
)
