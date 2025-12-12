package dev.jch0029987.libretibs.api.obj

import android.os.Parcelable
import dev.jch0029987.libretibs.db.obj.LocalPlaylistItem
import dev.jch0029987.libretibs.db.obj.SubscriptionsFeedItem
import dev.jch0029987.libretibs.db.obj.WatchHistoryItem
import dev.jch0029987.libretibs.extensions.toID
import dev.jch0029987.libretibs.extensions.toLocalDate
import dev.jch0029987.libretibs.helpers.ProxyHelper
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class StreamItem(
    val url: String? = null,
    val type: String? = null,
    var title: String? = null,
    var thumbnail: String? = null,
    val uploaderName: String? = null,
    val uploaderUrl: String? = null,
    val uploaderAvatar: String? = null,
    val uploadedDate: String? = null,
    val duration: Long? = null,
    val views: Long? = null,
    val uploaderVerified: Boolean? = null,
    val uploaded: Long = 0,
    val shortDescription: String? = null,
    val isShort: Boolean = false
) : Parcelable {
    val isLive get() = !isShort && ((duration == null) || (duration <= 0L))
    val isUpcoming get() = uploaded > System.currentTimeMillis()

    fun toLocalPlaylistItem(playlistId: String): LocalPlaylistItem {
        return LocalPlaylistItem(
            playlistId = playlistId.toInt(),
            videoId = url!!.toID(),
            title = title,
            thumbnailUrl = thumbnail?.let { ProxyHelper.unwrapUrl(it) },
            uploader = uploaderName,
            uploaderUrl = uploaderUrl,
            uploaderAvatar = uploaderAvatar?.let { ProxyHelper.unwrapUrl(it) },
            uploadDate = uploadedDate,
            duration = duration
        )
    }

    fun toFeedItem() = SubscriptionsFeedItem(
        videoId = url!!.toID(),
        title = title,
        thumbnail = thumbnail,
        uploaderName = uploaderName,
        uploaded = uploaded,
        uploaderAvatar = uploaderAvatar,
        uploaderUrl = uploaderUrl,
        duration = duration,
        uploaderVerified = uploaderVerified ?: false,
        shortDescription = shortDescription,
        views = views,
        isShort = isShort
    )
    
    fun toWatchHistoryItem(videoId: String) = WatchHistoryItem(
        videoId = videoId,
        title = title,
        uploadDate = uploaded.toLocalDate(),
        uploader = uploaderName,
        uploaderUrl = uploaderUrl?.toID(),
        uploaderAvatar = uploaderAvatar?.let { ProxyHelper.unwrapUrl(it) },
        thumbnailUrl = thumbnail?.let { ProxyHelper.unwrapUrl(it) },
        duration = duration
    )

    companion object {
        const val TYPE_STREAM = "stream"
        const val TYPE_CHANNEL = "channel"
        const val TYPE_PLAYLIST = "playlist"
    }
}
