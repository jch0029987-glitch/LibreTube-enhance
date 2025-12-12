package dev.jch0029987.libretibs.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import dev.jch0029987.libretibs.databinding.AllCaughtUpRowBinding
import dev.jch0029987.libretibs.databinding.TrendingRowBinding

class VideoCardsViewHolder : RecyclerView.ViewHolder {
    var trendingRowBinding: TrendingRowBinding? = null
    var allCaughtUpBinding: AllCaughtUpRowBinding? = null

    constructor(binding: TrendingRowBinding) : super(binding.root) {
        trendingRowBinding = binding
    }

    constructor(binding: AllCaughtUpRowBinding) : super(binding.root) {
        allCaughtUpBinding = binding
    }
}
