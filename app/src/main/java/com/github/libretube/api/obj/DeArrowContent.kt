package dev.jch0029987.libretibs.api.obj

import kotlinx.serialization.Serializable

@Serializable
data class DeArrowContent(
    val thumbnails: List<DeArrowThumbnail>,
    val titles: List<DeArrowTitle>,
    val randomTime: Float?,
    val videoDuration: Float?
)
