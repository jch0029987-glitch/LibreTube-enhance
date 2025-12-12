package dev.jch0029987.libretibs.obj

import kotlinx.serialization.Serializable

@Serializable
data class FreeTubeVideo(
    val videoId: String,
    val title: String,
    val author: String,
    val authorId: String,
    val lengthSeconds: Long,
    val type: String = "video"
)
