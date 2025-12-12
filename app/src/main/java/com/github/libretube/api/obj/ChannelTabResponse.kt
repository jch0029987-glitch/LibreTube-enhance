package dev.jch0029987.libretibs.api.obj

import kotlinx.serialization.Serializable

@Serializable
data class ChannelTabResponse(
    var content: List<ContentItem> = emptyList(),
    val nextpage: String? = null
)
