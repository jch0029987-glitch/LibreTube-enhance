package dev.jch0029987.libretibs.ui.sheets

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.NavHostFragment
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.api.obj.StreamItem
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.constants.PreferenceKeys
import dev.jch0029987.libretibs.db.DatabaseHelper
import dev.jch0029987.libretibs.db.DatabaseHolder
import dev.jch0029987.libretibs.db.obj.WatchPosition
import dev.jch0029987.libretibs.enums.ShareObjectType
import dev.jch0029987.libretibs.extensions.parcelable
import dev.jch0029987.libretibs.extensions.toID
import dev.jch0029987.libretibs.helpers.DownloadHelper
import dev.jch0029987.libretibs.helpers.NavigationHelper
import dev.jch0029987.libretibs.helpers.PlayerHelper
import dev.jch0029987.libretibs.helpers.PreferenceHelper
import dev.jch0029987.libretibs.obj.ShareData
import dev.jch0029987.libretibs.ui.activities.MainActivity
import dev.jch0029987.libretibs.ui.dialogs.AddToPlaylistDialog
import dev.jch0029987.libretibs.ui.dialogs.ShareDialog
import dev.jch0029987.libretibs.ui.fragments.SubscriptionsFragment
import dev.jch0029987.libretibs.util.PlayingQueue
import dev.jch0029987.libretibs.util.PlayingQueueMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Dialog with different options for a selected video.
 *
 * Needs the [streamItem] to load the content from the right video.
 */
class VideoOptionsBottomSheet : BaseBottomSheet() {
    private lateinit var streamItem: StreamItem

    override fun onCreate(savedInstanceState: Bundle?) {
        streamItem = arguments?.parcelable(IntentData.streamItem)!!
        val playlistId = arguments?.getString(IntentData.playlistId)

        val videoId = streamItem.url?.toID() ?: return

        setTitle(streamItem.title)

        val optionsList = mutableListOf<Int>()
        // these options are only available for other videos than the currently playing one
        if (PlayingQueue.getCurrent()?.url?.toID() != videoId) {
            optionsList += getOptionsForNotActivePlayback(videoId)
        }

        optionsList += listOf(R.string.addToPlaylist, R.string.download, R.string.share)
        if (streamItem.isLive) optionsList.remove(R.string.download)

        setSimpleItems(optionsList.map { getString(it) }) { which ->
            when (optionsList[which]) {
                // Start the background mode
                R.string.playOnBackground -> {
                    NavigationHelper.navigateVideo(
                        requireContext(),
                        videoId = videoId,
                        playlistId = playlistId,
                        audioOnlyPlayerRequested = true
                    )
                }
                // Add Video to Playlist Dialog
                R.string.addToPlaylist -> {
                    AddToPlaylistDialog().apply {
                        arguments = bundleOf(IntentData.videoInfo to streamItem)
                    }.show(
                        parentFragmentManager,
                        AddToPlaylistDialog::class.java.name
                    )
                }

                R.string.download -> {
                    DownloadHelper.startDownloadDialog(
                        requireContext(),
                        parentFragmentManager,
                        videoId
                    )
                }

                R.string.share -> {
                    val bundle = bundleOf(
                        IntentData.id to videoId,
                        IntentData.shareObjectType to ShareObjectType.VIDEO,
                        IntentData.shareData to ShareData(currentVideo = streamItem.title)
                    )
                    val newShareDialog = ShareDialog()
                    newShareDialog.arguments = bundle
                    // using parentFragmentManager is important here
                    newShareDialog.show(parentFragmentManager, ShareDialog::class.java.name)
                }

                R.string.play_next -> {
                    PlayingQueue.addAsNext(streamItem)
                }

                R.string.add_to_queue -> {
                    PlayingQueue.add(streamItem)
                }

                R.string.mark_as_watched -> {
                    val watchPosition = WatchPosition(videoId, Long.MAX_VALUE)
                    withContext(Dispatchers.IO) {
                        DatabaseHolder.Database.watchPositionDao().insert(watchPosition)

                        if (PlayerHelper.watchHistoryEnabled) {
                            DatabaseHelper.addToWatchHistory(streamItem.toWatchHistoryItem(videoId))
                        }
                    }
                    if (PreferenceHelper.getBoolean(PreferenceKeys.HIDE_WATCHED_FROM_FEED, false)) {
                        // get the host fragment containing the current fragment
                        val navHostFragment = (context as MainActivity).supportFragmentManager
                            .findFragmentById(R.id.fragment) as NavHostFragment?
                        // get the current fragment
                        val fragment = navHostFragment?.childFragmentManager?.fragments
                            ?.firstOrNull() as? SubscriptionsFragment
                        fragment?.removeItem(videoId)
                    }
                    setFragmentResult(VIDEO_OPTIONS_SHEET_REQUEST_KEY, bundleOf())
                }

                R.string.mark_as_unwatched -> {
                    withContext(Dispatchers.IO) {
                        DatabaseHolder.Database.watchPositionDao().deleteByVideoId(videoId)
                        DatabaseHolder.Database.watchHistoryDao().deleteByVideoId(videoId)
                    }
                    setFragmentResult(VIDEO_OPTIONS_SHEET_REQUEST_KEY, bundleOf())
                }
            }
        }

        super.onCreate(savedInstanceState)
    }

    private fun getOptionsForNotActivePlayback(videoId: String): List<Int> {
        // List that stores the different menu options. In the future could be add more options here.
        val optionsList = mutableListOf(R.string.playOnBackground)

        // Check whether the player is running and add queue options
        if (PlayingQueue.isNotEmpty() && PlayingQueue.queueMode == PlayingQueueMode.ONLINE) {
            optionsList += R.string.play_next
            optionsList += R.string.add_to_queue
        }

        // show the mark as watched or unwatched option if watch positions are enabled
        if (PlayerHelper.watchPositionsAny || PlayerHelper.watchHistoryEnabled) {
            val watchHistoryEntry = runBlocking(Dispatchers.IO) {
                DatabaseHolder.Database.watchHistoryDao().findById(videoId)
            }

            val position = DatabaseHelper.getWatchPositionBlocking(videoId) ?: 0
            val isCompleted = DatabaseHelper.isVideoWatched(position, streamItem.duration ?: 0)
            if (position != 0L || watchHistoryEntry != null) {
                optionsList += R.string.mark_as_unwatched
            }

            if (!isCompleted || watchHistoryEntry == null) {
                optionsList += R.string.mark_as_watched
            }
        }

        return optionsList
    }

    companion object {
        const val VIDEO_OPTIONS_SHEET_REQUEST_KEY = "video_options_sheet_request_key"
    }
}
