package dev.jch0029987.libretibs.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import dev.jch0029987.libretibs.databinding.ChannelRowBinding
import dev.jch0029987.libretibs.databinding.PlaylistsRowBinding
import dev.jch0029987.libretibs.databinding.VideoRowBinding

class SearchViewHolder : RecyclerView.ViewHolder {
    var videoRowBinding: VideoRowBinding? = null
    var channelRowBinding: ChannelRowBinding? = null
    var playlistRowBinding: PlaylistsRowBinding? = null

    constructor(binding: VideoRowBinding) : super(binding.root) {
        videoRowBinding = binding
    }

    constructor(binding: ChannelRowBinding) : super(binding.root) {
        channelRowBinding = binding
    }

    constructor(binding: PlaylistsRowBinding) : super(binding.root) {
        playlistRowBinding = binding
    }
}
