package dev.jch0029987.libretibs.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.ListAdapter
import dev.jch0029987.libretibs.api.PlaylistsHelper
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.databinding.CarouselPlaylistThumbnailBinding
import dev.jch0029987.libretibs.helpers.ImageHelper
import dev.jch0029987.libretibs.helpers.NavigationHelper
import dev.jch0029987.libretibs.ui.adapters.callbacks.DiffUtilItemCallback
import dev.jch0029987.libretibs.ui.base.BaseActivity
import dev.jch0029987.libretibs.ui.sheets.PlaylistOptionsBottomSheet
import dev.jch0029987.libretibs.ui.viewholders.CarouselPlaylistViewHolder

data class CarouselPlaylist(
    val id: String,
    val title: String?,
    val thumbnail: String?
)

class CarouselPlaylistAdapter : ListAdapter<CarouselPlaylist, CarouselPlaylistViewHolder>(
    DiffUtilItemCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarouselPlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CarouselPlaylistViewHolder(CarouselPlaylistThumbnailBinding.inflate(layoutInflater))
    }

    override fun onBindViewHolder(
        holder: CarouselPlaylistViewHolder,
        position: Int
    ) {
        val item = getItem(position)!!

        with(holder.binding) {
            playlistName.text = item.title
            ImageHelper.loadImage(item.thumbnail, thumbnail)

            val type = PlaylistsHelper.getPlaylistType(item.id)
            root.setOnClickListener {
                NavigationHelper.navigatePlaylist(root.context, item.id, type)
            }

            root.setOnLongClickListener {
                val playlistOptionsDialog = PlaylistOptionsBottomSheet()
                playlistOptionsDialog.arguments = bundleOf(
                    IntentData.playlistId to item.id,
                    IntentData.playlistName to item.title,
                    IntentData.playlistType to type
                )
                playlistOptionsDialog.show((root.context as BaseActivity).supportFragmentManager)

                true
            }
        }
    }
}