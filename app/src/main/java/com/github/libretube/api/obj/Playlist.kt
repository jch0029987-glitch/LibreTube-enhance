package dev.jch0029987.libretibs.api.obj

import dev.jch0029987.libretibs.db.obj.PlaylistBookmark
import dev.jch0029987.libretibs.helpers.ProxyHelper
import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val name: String? = null,
    var description: String? = null,
    val thumbnailUrl: String? = null,
    val bannerUrl: String? = null,
    val nextpage: String? = null,
    val uploader: String? = null,
    val uploaderUrl: String? = null,
    val uploaderAvatar: String? = null,
    val videos: Int = 0,
    var relatedStreams: List<StreamItem> = emptyList()
) {
    fun toPlaylistBookmark(playlistId: String): PlaylistBookmark {
        return PlaylistBookmark(
            playlistId = playlistId,
            playlistName = name,
            thumbnailUrl = thumbnailUrl?.let { ProxyHelper.unwrapUrl(it) },
            uploader = uploader,
            uploaderAvatar = uploaderAvatar?.let { ProxyHelper.unwrapUrl(it) },
            uploaderUrl = uploaderUrl,
            videos = videos
        )
    }
}
