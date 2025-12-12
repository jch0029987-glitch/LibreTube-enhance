package dev.jch0029987.libretibs.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ListAdapter
import dev.jch0029987.libretibs.api.obj.StreamItem
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.databinding.AllCaughtUpRowBinding
import dev.jch0029987.libretibs.databinding.TrendingRowBinding
import dev.jch0029987.libretibs.extensions.dpToPx
import dev.jch0029987.libretibs.extensions.toID
import dev.jch0029987.libretibs.helpers.ImageHelper
import dev.jch0029987.libretibs.helpers.NavigationHelper
import dev.jch0029987.libretibs.ui.adapters.callbacks.DiffUtilItemCallback
import dev.jch0029987.libretibs.ui.base.BaseActivity
import dev.jch0029987.libretibs.ui.extensions.setFormattedDuration
import dev.jch0029987.libretibs.ui.extensions.setWatchProgressLength
import dev.jch0029987.libretibs.ui.sheets.VideoOptionsBottomSheet
import dev.jch0029987.libretibs.ui.viewholders.VideoCardsViewHolder
import dev.jch0029987.libretibs.util.DeArrowUtil
import dev.jch0029987.libretibs.util.TextUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoCardsAdapter(private val columnWidthDp: Float? = null) :
    ListAdapter<StreamItem, VideoCardsViewHolder>(DiffUtilItemCallback()) {

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].type == CAUGHT_UP_STREAM_TYPE) CAUGHT_UP_TYPE else NORMAL_TYPE
    }

    fun removeItemById(videoId: String) {
        val index = currentList.indexOfFirst {
            it.url?.toID() == videoId
        }.takeIf { it > 0 } ?: return
        val updatedList = currentList.toMutableList().also {
            it.removeAt(index)
        }

        submitList(updatedList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoCardsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when {
            viewType == CAUGHT_UP_TYPE -> VideoCardsViewHolder(
                AllCaughtUpRowBinding.inflate(layoutInflater, parent, false)
            )

            else -> VideoCardsViewHolder(
                TrendingRowBinding.inflate(layoutInflater, parent, false)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VideoCardsViewHolder, position: Int) {
        val video = getItem(holder.bindingAdapterPosition)
        val videoId = video.url.orEmpty().toID()

        val context = (holder.trendingRowBinding ?: holder.allCaughtUpBinding)!!.root.context
        val activity = (context as BaseActivity)
        val fragmentManager = activity.supportFragmentManager

        holder.trendingRowBinding?.apply {
            // set a fixed width for better visuals
            if (columnWidthDp != null) {
                root.updateLayoutParams {
                    width = columnWidthDp.dpToPx()
                }
            }
            watchProgress.setWatchProgressLength(videoId, video.duration ?: 0L)

            textViewTitle.text = video.title
            textViewChannel.text = TextUtils.formatViewsString(
                root.context,
                video.views ?: -1,
                video.uploaded,
                video.uploaderName
            )

            video.duration?.let {
                thumbnailDuration.setFormattedDuration(
                    it,
                    video.isShort,
                    video.uploaded
                )
            }
            ImageHelper.loadImage(video.thumbnail, thumbnail)

            if (video.uploaderAvatar != null) {
                channelImageContainer.isVisible = true
                ImageHelper.loadImage(video.uploaderAvatar, channelImage, true)
                channelImage.setOnClickListener {
                    NavigationHelper.navigateChannel(root.context, video.uploaderUrl)
                }
            } else {
                channelImageContainer.isGone = true
                textViewChannel.setOnClickListener {
                    NavigationHelper.navigateChannel(root.context, video.uploaderUrl)
                }
            }
            root.setOnClickListener {
                NavigationHelper.navigateVideo(root.context, videoId)
            }

            root.setOnLongClickListener {
                fragmentManager.setFragmentResultListener(
                    VideoOptionsBottomSheet.VIDEO_OPTIONS_SHEET_REQUEST_KEY,
                    activity
                ) { _, _ ->
                    notifyItemChanged(position)
                }
                val sheet = VideoOptionsBottomSheet()
                sheet.arguments = bundleOf(IntentData.streamItem to video)
                sheet.show(fragmentManager, VideoCardsAdapter::class.java.name)
                true
            }

            CoroutineScope(Dispatchers.IO).launch {
                DeArrowUtil.deArrowVideoId(videoId)?.let { (title, thumbnail) ->
                    withContext(Dispatchers.Main) {
                        if (title != null) this@apply.textViewTitle.text = title
                        if (thumbnail != null) ImageHelper.loadImage(thumbnail, this@apply.thumbnail)
                    }
                }
            }
        }
    }

    companion object {
        private const val NORMAL_TYPE = 0
        private const val CAUGHT_UP_TYPE = 1

        const val CAUGHT_UP_STREAM_TYPE = "caught"
    }
}
