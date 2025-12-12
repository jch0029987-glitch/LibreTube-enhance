package dev.jch0029987.libretibs.api.obj

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeArrowTitle(
    @SerialName("UUID") val uuid: String,
    val locked: Boolean,
    val original: Boolean,
    val title: String,
    val votes: Int
)
