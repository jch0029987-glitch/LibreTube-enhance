package dev.jch0029987.libretibs.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import dev.jch0029987.libretibs.api.obj.StreamItem
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.databinding.VideoRowBinding
import dev.jch0029987.libretibs.db.DatabaseHolder
import dev.jch0029987.libretibs.extensions.toID
import dev.jch0029987.libretibs.helpers.ImageHelper
import dev.jch0029987.libretibs.helpers.NavigationHelper
import dev.jch0029987.libretibs.ui.adapters.callbacks.DiffUtilItemCallback
import dev.jch0029987.libretibs.ui.base.BaseActivity
import dev.jch0029987.libretibs.ui.extensions.setFormattedDuration
import dev.jch0029987.libretibs.ui.extensions.setWatchProgressLength
import dev.jch0029987.libretibs.ui.sheets.VideoOptionsBottomSheet
import dev.jch0029987.libretibs.ui.viewholders.VideosViewHolder
import dev.jch0029987.libretibs.util.DeArrowUtil
import dev.jch0029987.libretibs.util.TextUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideosAdapter(
    private val showChannelInfo: Boolean = true
) : ListAdapter<StreamItem, VideosViewHolder>(DiffUtilItemCallback()) {

    fun insertItems(newItems: List<StreamItem>) {
        val updatedList = currentList.toMutableList().also {
            it.addAll(newItems)
        }

        submitList(updatedList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = VideoRowBinding.inflate(layoutInflater, parent, false)
        return VideosViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val video = getItem(holder.bindingAdapterPosition)
        val videoId = video.url.orEmpty().toID()

        val context = holder.binding.root.context
        val activity = (context as BaseActivity)
        val fragmentManager = activity.supportFragmentManager

        with(holder.binding) {
            videoTitle.text = video.title
            videoInfo.text = TextUtils.formatViewsString(root.context, video.views ?: -1, video.uploaded)

            video.duration?.let { thumbnailDuration.setFormattedDuration(it, video.isShort, video.uploaded) }
            watchProgress.setWatchProgressLength(videoId, video.duration ?: 0L)
            ImageHelper.loadImage(video.thumbnail, thumbnail)

            if (showChannelInfo) {
                ImageHelper.loadImage(video.uploaderAvatar, channelImage, true)
                channelName.text = video.uploaderName

                channelContainer.setOnClickListener {
                    NavigationHelper.navigateChannel(root.context, video.uploaderUrl)
                }
            } else {
                channelImageContainer.isGone = true
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
                sheet.show(fragmentManager, VideosAdapter::class.java.name)
                true
            }

            CoroutineScope(Dispatchers.IO).launch {
                val isDownloaded =
                    DatabaseHolder.Database.downloadDao().exists(videoId)

                withContext(Dispatchers.Main) {
                    downloadBadge.isVisible = isDownloaded
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                DeArrowUtil.deArrowVideoId(videoId)?.let { (title, thumbnail) ->
                    withContext(Dispatchers.Main) {
                        if (title != null) holder.binding.videoTitle.text = title
                        if (thumbnail != null) ImageHelper.loadImage(thumbnail, holder.binding.thumbnail)
                    }
                }
            }
        }
    }
}
