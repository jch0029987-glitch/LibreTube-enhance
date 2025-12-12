package dev.jch0029987.libretibs.obj

import android.graphics.Bitmap
import dev.jch0029987.libretibs.api.obj.Streams

data class DownloadedFile(
    val name: String,
    val size: Long,
    var metadata: Streams? = null,
    var thumbnail: Bitmap? = null
)
