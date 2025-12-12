package dev.jch0029987.libretibs.api.obj

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    var items: List<ContentItem> = emptyList(),
    val nextpage: String? = null,
    val suggestion: String? = null,
    val corrected: Boolean = false
)
