package dev.jch0029987.libretibs.ui.sheets

import android.os.Bundle
import android.view.View
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.databinding.DialogStatsBinding
import dev.jch0029987.libretibs.extensions.parcelable
import dev.jch0029987.libretibs.helpers.ClipboardHelper
import dev.jch0029987.libretibs.obj.VideoStats

class StatsSheet : ExpandedBottomSheet(R.layout.dialog_stats) {
    private lateinit var stats: VideoStats

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stats = arguments?.parcelable(IntentData.videoStats)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = DialogStatsBinding.bind(view)
        binding.videoId.setText(stats.videoId)
        binding.videoIdCopy.setEndIconOnClickListener {
            ClipboardHelper.save(requireContext(), "text", stats.videoId)
        }
        binding.videoInfo.setText(stats.videoInfo)
        binding.audioInfo.setText(stats.audioInfo)
        binding.videoQuality.setText(stats.videoQuality)
    }
}
