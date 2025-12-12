package dev.jch0029987.libretibs.ui.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.api.PlaylistsHelper
import dev.jch0029987.libretibs.api.obj.StreamItem
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.databinding.VideoRowBinding
import dev.jch0029987.libretibs.db.DatabaseHolder
import dev.jch0029987.libretibs.enums.PlaylistType
import dev.jch0029987.libretibs.extensions.TAG
import dev.jch0029987.libretibs.extensions.toID
import dev.jch0029987.libretibs.extensions.toastFromMainDispatcher
import dev.jch0029987.libretibs.helpers.ImageHelper
import dev.jch0029987.libretibs.helpers.NavigationHelper
import dev.jch0029987.libretibs.ui.base.BaseActivity
import dev.jch0029987.libretibs.ui.extensions.setFormattedDuration
import dev.jch0029987.libretibs.ui.extensions.setWatchProgressLength
import dev.jch0029987.libretibs.ui.sheets.VideoOptionsBottomSheet
import dev.jch0029987.libretibs.ui.sheets.VideoOptionsBottomSheet.Companion.VIDEO_OPTIONS_SHEET_REQUEST_KEY
import dev.jch0029987.libretibs.ui.viewholders.PlaylistViewHolder
import dev.jch0029987.libretibs.util.TextUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @param originalFeed original, unsorted feed, needed in order to delete the proper video from
 * playlists
 */
class PlaylistAdapter(
    val originalFeed: MutableList<StreamItem>,
    private val sortedFeed: MutableList<StreamItem>,
    private val playlistId: String,
    private val playlistType: PlaylistType,
    private val onVideoClick: (StreamItem) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var visibleCount = minOf(20, sortedFeed.size)

    override fun getItemCount(): Int {
        return when (playlistType) {
            PlaylistType.PUBLIC -> sortedFeed.size
            else -> minOf(visibleCount, sortedFeed.size)
        }
    }

    fun updateItems(newItems: List<StreamItem>) {
        val oldSize = sortedFeed.size
        sortedFeed.addAll(newItems)
        notifyItemRangeInserted(oldSize, sortedFeed.size)
    }

    fun showMoreItems() {
        val oldSize = visibleCount
        visibleCount += minOf(10, sortedFeed.size - oldSize)
        if (visibleCount == oldSize) return
        notifyItemRangeInserted(oldSize, visibleCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = VideoRowBinding.inflate(layoutInflater, parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val streamItem = sortedFeed[position]
        val videoId = streamItem.url!!.toID()

        holder.binding.apply {
            videoTitle.text = streamItem.title
            videoInfo.text = TextUtils.formatViewsString(root.context, streamItem.views ?: -1, streamItem.uploaded)
            videoInfo.maxLines = 2

            // piped does not load channel avatars for playlist views
            channelImageContainer.isGone = true
            channelName.text = streamItem.uploaderName

            ImageHelper.loadImage(streamItem.thumbnail, thumbnail)
            thumbnailDuration.setFormattedDuration(streamItem.duration ?: -1, streamItem.isShort, streamItem.uploaded)

            root.setOnClickListener {
                onVideoClick(streamItem)
            }

            val activity = (root.context as BaseActivity)
            val fragmentManager = activity.supportFragmentManager
            root.setOnLongClickListener {
                fragmentManager.setFragmentResultListener(
                    VIDEO_OPTIONS_SHEET_REQUEST_KEY,
                    activity
                ) { _, _ ->
                    notifyItemChanged(position)
                }
                VideoOptionsBottomSheet().apply {
                    arguments = bundleOf(
                        IntentData.streamItem to streamItem,
                        IntentData.playlistId to playlistId
                    )
                }
                    .show(fragmentManager, VideoOptionsBottomSheet::class.java.name)
                true
            }

            if (!streamItem.uploaderUrl.isNullOrBlank()) {
                channelContainer.setOnClickListener {
                    NavigationHelper.navigateChannel(root.context, streamItem.uploaderUrl)
                }
            }

            streamItem.duration?.let { watchProgress.setWatchProgressLength(videoId, it) }

            CoroutineScope(Dispatchers.IO).launch {
                val isDownloaded =
                    DatabaseHolder.Database.downloadDao().exists(videoId)

                withContext(Dispatchers.Main) {
                    downloadBadge.isVisible = isDownloaded
                }
            }
        }
    }

    fun removeFromPlaylist(rootView: View, sortedFeedPosition: Int) {
        val video = sortedFeed[sortedFeedPosition]

        // get the index of the video in the playlist
        // could vary due to playlist sorting by the user
        val originalPlaylistPosition = originalFeed
            .indexOfFirst { it.url == video.url }
            .takeIf { it >= 0 } ?: return

        sortedFeed.removeAt(sortedFeedPosition)
        originalFeed.removeAt(originalPlaylistPosition)
        visibleCount--

        (rootView.context as Activity).runOnUiThread {
            notifyItemRemoved(sortedFeedPosition)
            notifyItemRangeChanged(sortedFeedPosition, itemCount)
        }
        val appContext = rootView.context.applicationContext

        // try to remove the video from the playlist and show an undo snackbar if successful
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    PlaylistsHelper.removeFromPlaylist(playlistId, originalPlaylistPosition)
                }

                val shortTitle = TextUtils.limitTextToLength(video.title.orEmpty(), 50)
                val snackBarText = rootView.context.getString(
                    R.string.successfully_removed_from_playlist,
                    shortTitle
                )
                Snackbar.make(rootView, snackBarText, Snackbar.LENGTH_LONG)
                    .setTextMaxLines(3)
                    .setAction(R.string.undo) {
                        reAddToPlaylist(
                            appContext,
                            video,
                            sortedFeedPosition,
                            originalPlaylistPosition
                        )
                    }
                    .show()
            } catch (e: Exception) {
                Log.e(TAG(), e.toString())
                appContext.toastFromMainDispatcher(R.string.unknown_error)
            }
        }
    }

    private fun reAddToPlaylist(
        context: Context,
        streamItem: StreamItem,
        sortedFeedPosition: Int,
        originalPlaylistPosition: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                PlaylistsHelper.addToPlaylist(playlistId, streamItem)
                sortedFeed.add(sortedFeedPosition, streamItem)
                originalFeed.add(originalPlaylistPosition, streamItem)
                visibleCount++

                withContext(Dispatchers.Main) {
                    notifyItemInserted(sortedFeedPosition)
                }
            } catch (e: Exception) {
                Log.e(TAG(), e.toString())
                context.toastFromMainDispatcher(R.string.unknown_error)
            }
        }
    }
}
