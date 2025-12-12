package dev.jch0029987.libretibs.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.api.MediaServiceRepository
import dev.jch0029987.libretibs.api.obj.StreamItem
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.extensions.toastFromMainDispatcher
import dev.jch0029987.libretibs.helpers.IntentHelper
import dev.jch0029987.libretibs.helpers.PreferenceHelper
import dev.jch0029987.libretibs.ui.base.BaseActivity
import dev.jch0029987.libretibs.ui.dialogs.AddToPlaylistDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddToPlaylistActivity : BaseActivity() {
    override val isDialogActivity: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val videoId = intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            IntentHelper.resolveType(it.toUri())
        }?.getStringExtra(IntentData.videoId)

        if (videoId == null) {
            finish()
            return
        }

        supportFragmentManager.setFragmentResultListener(
            AddToPlaylistDialog.ADD_TO_PLAYLIST_DIALOG_DISMISSED_KEY,
            this
        ) { _, _ -> finish() }

        lifecycleScope.launch(Dispatchers.IO) {
            val videoInfo = if (PreferenceHelper.getToken().isEmpty()) {
                try {
                    MediaServiceRepository.instance.getStreams(videoId).toStreamItem(videoId)
                } catch (e: Exception) {
                    toastFromMainDispatcher(R.string.unknown_error)
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                    return@launch
                }
            } else {
                StreamItem(videoId)
            }

            withContext(Dispatchers.Main) {
                AddToPlaylistDialog().apply {
                    arguments = bundleOf(IntentData.videoInfo to videoInfo)
                }.show(supportFragmentManager, null)
            }
        }
    }
}