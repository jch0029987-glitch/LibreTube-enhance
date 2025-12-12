package dev.jch0029987.libretibs.extensions

import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import dev.jch0029987.libretibs.api.JsonHelper
import dev.jch0029987.libretibs.api.obj.Streams
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.db.obj.DownloadChapter
import dev.jch0029987.libretibs.db.obj.DownloadWithItems
import kotlinx.serialization.encodeToString

@OptIn(UnstableApi::class)
fun MediaItem.Builder.setMetadata(streams: Streams, videoId: String) = apply {
    // Avoid reaching the max parcelable size of 1MB for binder transactions.
    val clearedStreams = streams.copy(audioStreams = emptyList(), videoStreams = emptyList())
    val extras = bundleOf(
        MediaMetadataCompat.METADATA_KEY_TITLE to streams.title,
        MediaMetadataCompat.METADATA_KEY_ARTIST to streams.uploader,
        IntentData.videoId to videoId,
        // JSON-encode as work-around for https://github.com/androidx/media/issues/564
        IntentData.streams to JsonHelper.json.encodeToString(clearedStreams),
        IntentData.chapters to JsonHelper.json.encodeToString(streams.chapters)
    )
    setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(streams.title)
            .setArtist(streams.uploader)
            .setDurationMs(streams.duration.times(1000))
            .setArtworkUri(streams.thumbnailUrl.toUri())
            .setComposer(streams.uploaderUrl.toID())
            .setExtras(extras)
            // send a unique timestamp to notify that the metadata changed, even if playing the same video twice
            .setTrackNumber(System.currentTimeMillis().mod(Int.MAX_VALUE))
            .build()
    )
}

@OptIn(UnstableApi::class)
fun MediaItem.Builder.setMetadata(downloadWithItems: DownloadWithItems) = apply {
    val (download, _, downloadChapters) = downloadWithItems
    val chapters = downloadChapters.map(DownloadChapter::toChapterSegment)

    val extras = bundleOf(
        MediaMetadataCompat.METADATA_KEY_TITLE to download.title,
        MediaMetadataCompat.METADATA_KEY_ARTIST to download.uploader,
        IntentData.videoId to download.videoId,
        IntentData.chapters to JsonHelper.json.encodeToString(chapters)
    )
    setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(download.title)
            .setArtist(download.uploader)
            .setDurationMs(download.duration?.times(1000))
            .setArtworkUri(download.thumbnailPath?.toAndroidUri())
            .setExtras(extras)
            // send a unique timestamp to notify that the metadata changed, even if playing the same video twice
            .setTrackNumber(System.currentTimeMillis().mod(Int.MAX_VALUE))
            .build()
    )
}
