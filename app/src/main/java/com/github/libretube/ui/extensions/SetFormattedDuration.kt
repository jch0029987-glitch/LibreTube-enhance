package dev.jch0029987.libretibs.ui.extensions

import android.text.format.DateUtils
import android.widget.TextView
import dev.jch0029987.libretibs.R

fun TextView.setFormattedDuration(duration: Long, isShort: Boolean?, uploadDate: Long) {
    this.text = when {
        isShort == true -> context.getString(R.string.yt_shorts)
        duration < 0L -> context.getString(R.string.live)
        uploadDate > System.currentTimeMillis() -> context.getString(R.string.upcoming)
        else -> DateUtils.formatElapsedTime(duration)
    }
}
