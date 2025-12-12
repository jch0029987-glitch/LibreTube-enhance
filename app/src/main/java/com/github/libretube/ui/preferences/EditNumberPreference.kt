package dev.jch0029987.libretibs.ui.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import dev.jch0029987.libretibs.ui.base.BasePreferenceFragment

/**
 * [EditTextPreference] that only allows numeric input.
 * The actual functionality is done in [BasePreferenceFragment].
 */
class EditNumberPreference(context: Context, attributeSet: AttributeSet?): EditTextPreference(context, attributeSet)