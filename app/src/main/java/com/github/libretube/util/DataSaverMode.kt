package dev.jch0029987.libretibs.util

import android.content.Context
import dev.jch0029987.libretibs.constants.PreferenceKeys
import dev.jch0029987.libretibs.helpers.NetworkHelper
import dev.jch0029987.libretibs.helpers.PreferenceHelper

object DataSaverMode {
    fun isEnabled(context: Context): Boolean {
        val pref = PreferenceHelper.getString(PreferenceKeys.DATA_SAVER_MODE, "disabled")
        return when (pref) {
            "enabled" -> true
            "disabled" -> false
            "metered" -> NetworkHelper.isNetworkMetered(context)
            else -> throw IllegalArgumentException()
        }
    }
}
