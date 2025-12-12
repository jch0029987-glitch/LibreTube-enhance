package dev.jch0029987.libretibs.obj

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FreetubeSubscriptions(
    @SerialName("_id") val id: String = "",
    val name: String = "",
    val subscriptions: List<FreetubeSubscription> = emptyList()
)
