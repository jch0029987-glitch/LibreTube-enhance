package dev.jch0029987.libretibs.api.obj

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val error: String? = null,
    val message: String? = null
)
