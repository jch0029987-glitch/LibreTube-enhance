package dev.jch0029987.libretibs.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.enums.PlaylistType
import dev.jch0029987.libretibs.helpers.IntentHelper
import dev.jch0029987.libretibs.ui.base.BaseActivity
import dev.jch0029987.libretibs.ui.dialogs.DownloadDialog
import dev.jch0029987.libretibs.ui.dialogs.DownloadPlaylistDialog

class DownloadActivity : BaseActivity() {
    override val isDialogActivity: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentData = intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            IntentHelper.resolveType(it.toUri())
        }

        val videoId = intentData?.getStringExtra(IntentData.videoId)
        val playlistId = intentData?.getStringExtra(IntentData.playlistId)
        if (videoId != null) {
            supportFragmentManager.setFragmentResultListener(
                DownloadDialog.DOWNLOAD_DIALOG_DISMISSED_KEY,
                this
            ) { _, _ -> finish() }

            DownloadDialog().apply {
                arguments = bundleOf(IntentData.videoId to videoId)
            }.show(supportFragmentManager, null)
        } else if (playlistId != null) {
            DownloadPlaylistDialog().apply {
                arguments = bundleOf(
                    IntentData.playlistId to playlistId,
                    IntentData.playlistType to PlaylistType.PUBLIC,
                    IntentData.playlistName to intent.getStringExtra(Intent.EXTRA_TITLE)
                )
            }.show(supportFragmentManager, null)
        } else {
            finish()
        }
    }
}