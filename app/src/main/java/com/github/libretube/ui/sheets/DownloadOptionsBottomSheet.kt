package dev.jch0029987.libretibs.ui.sheets

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.api.obj.StreamItem
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.enums.ShareObjectType
import dev.jch0029987.libretibs.extensions.parcelable
import dev.jch0029987.libretibs.extensions.serializable
import dev.jch0029987.libretibs.extensions.toID
import dev.jch0029987.libretibs.helpers.BackgroundHelper
import dev.jch0029987.libretibs.helpers.ContextHelper
import dev.jch0029987.libretibs.helpers.NavigationHelper
import dev.jch0029987.libretibs.obj.ShareData
import dev.jch0029987.libretibs.ui.activities.NoInternetActivity
import dev.jch0029987.libretibs.ui.dialogs.ShareDialog
import dev.jch0029987.libretibs.ui.fragments.DownloadTab
import dev.jch0029987.libretibs.util.PlayingQueue
import dev.jch0029987.libretibs.util.PlayingQueueMode

class DownloadOptionsBottomSheet : BaseBottomSheet() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val streamItem = arguments?.parcelable<StreamItem>(IntentData.streamItem)!!
        val videoId = streamItem.url!!.toID()
        val downloadTab = arguments?.serializable<DownloadTab>(IntentData.downloadTab)!!

        val options = mutableListOf(
            R.string.playOnBackground,
            R.string.share,
            R.string.delete
        )

        // can't navigate to video while in offline activity
        if (ContextHelper.tryUnwrapActivity<NoInternetActivity>(requireContext()) == null) {
            options += R.string.go_to_video
        }

        val isSelectedVideoCurrentlyPlaying = PlayingQueue.getCurrent()?.url?.toID() == videoId
        if (!isSelectedVideoCurrentlyPlaying && PlayingQueue.isNotEmpty() && PlayingQueue.queueMode == PlayingQueueMode.OFFLINE) {
            options += R.string.play_next
            options += R.string.add_to_queue
        }

        setSimpleItems(options.map { getString(it) }) { which ->
            when (options[which]) {
                R.string.playOnBackground -> {
                    BackgroundHelper.playOnBackgroundOffline(requireContext(), videoId, downloadTab)
                }

                R.string.go_to_video -> {
                    NavigationHelper.navigateVideo(requireContext(), videoId = videoId)
                }

                R.string.share -> {
                    val shareData = ShareData(currentVideo = videoId)
                    val bundle = bundleOf(
                        IntentData.id to videoId,
                        IntentData.shareObjectType to ShareObjectType.VIDEO,
                        IntentData.shareData to shareData
                    )
                    val newShareDialog = ShareDialog()
                    newShareDialog.arguments = bundle
                    newShareDialog.show(parentFragmentManager, null)
                }

                R.string.delete -> {
                    setFragmentResult(DELETE_DOWNLOAD_REQUEST_KEY, bundleOf())
                    dialog?.dismiss()
                }

                R.string.play_next -> {
                    PlayingQueue.addAsNext(streamItem)
                }

                R.string.add_to_queue -> {
                    PlayingQueue.add(streamItem)
                }
            }
        }

        super.onCreate(savedInstanceState)
    }

    companion object {
        const val DELETE_DOWNLOAD_REQUEST_KEY = "delete_download_request_key"
    }
}
