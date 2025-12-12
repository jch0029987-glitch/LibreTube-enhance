package dev.jch0029987.libretibs.ui.preferences

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.constants.PreferenceKeys
import dev.jch0029987.libretibs.helpers.PreferenceHelper
import dev.jch0029987.libretibs.ui.adapters.IconsSheetAdapter
import dev.jch0029987.libretibs.ui.base.BasePreferenceFragment
import dev.jch0029987.libretibs.ui.dialogs.NavBarOptionsDialog
import dev.jch0029987.libretibs.ui.dialogs.RequireRestartDialog
import dev.jch0029987.libretibs.ui.sheets.IconsBottomSheet
import com.google.android.material.color.DynamicColors

class AppearanceSettings : BasePreferenceFragment() {
    override val titleResourceId: Int = R.string.appearance
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.appearance_settings, rootKey)

        val themeToggle = findPreference<ListPreference>(PreferenceKeys.THEME_MODE)
        themeToggle?.setOnPreferenceChangeListener { _, _ ->
            RequireRestartDialog().show(childFragmentManager, RequireRestartDialog::class.java.name)
            true
        }

        val pureTheme = findPreference<SwitchPreferenceCompat>(PreferenceKeys.PURE_THEME)
        pureTheme?.setOnPreferenceChangeListener { _, _ ->
            RequireRestartDialog().show(childFragmentManager, RequireRestartDialog::class.java.name)
            true
        }

        val accentColor = findPreference<ListPreference>(PreferenceKeys.ACCENT_COLOR)
        updateAccentColorValues(accentColor!!)
        accentColor.setOnPreferenceChangeListener { _, _ ->
            RequireRestartDialog().show(childFragmentManager, RequireRestartDialog::class.java.name)
            true
        }

        val changeIcon = findPreference<Preference>(PreferenceKeys.APP_ICON)
        val iconPref = PreferenceHelper.getString(
            PreferenceKeys.APP_ICON,
            IconsSheetAdapter.Companion.AppIcon.Default.activityAlias
        )
        IconsSheetAdapter.availableIcons.firstOrNull { it.activityAlias == iconPref }?.let {
            changeIcon?.summary = getString(it.nameResource)
        }
        changeIcon?.setOnPreferenceClickListener {
            IconsBottomSheet().show(childFragmentManager)
            true
        }

        val labelVisibilityMode = findPreference<ListPreference>(PreferenceKeys.LABEL_VISIBILITY)
        labelVisibilityMode?.setOnPreferenceChangeListener { _, _ ->
            RequireRestartDialog().show(childFragmentManager, RequireRestartDialog::class.java.name)
            true
        }

        val navBarOptions = findPreference<Preference>(PreferenceKeys.NAVBAR_ITEMS)
        navBarOptions?.setOnPreferenceClickListener {
            NavBarOptionsDialog().show(childFragmentManager, null)
            true
        }
    }

    /**
     * Remove material you from accent color option if not available
     */
    private fun updateAccentColorValues(pref: ListPreference) {
        if (!DynamicColors.isDynamicColorAvailable()) {
            pref.entries = pref.entries.toList().subList(1, pref.entries.size).toTypedArray()
            pref.entryValues = pref.entryValues.toList().subList(1, pref.entryValues.size).toTypedArray()
        }
    }
}
