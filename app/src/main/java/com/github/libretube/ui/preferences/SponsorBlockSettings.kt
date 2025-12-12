package dev.jch0029987.libretibs.ui.preferences

import android.os.Bundle
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.ui.base.BasePreferenceFragment

class SponsorBlockSettings : BasePreferenceFragment() {
    override val titleResourceId: Int = R.string.sponsorblock

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.sponsorblock_settings, rootKey)
    }
}
