package dev.jch0029987.libretibs.ui.preferences

import android.os.Bundle
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.ui.base.BasePreferenceFragment

class AudioVideoSettings : BasePreferenceFragment() {
    override val titleResourceId: Int = R.string.audio_video

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.audio_video_settings, rootKey)
    }
}
