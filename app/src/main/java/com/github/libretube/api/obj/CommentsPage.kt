package dev.jch0029987.libretibs.api.obj

import kotlinx.serialization.Serializable

@Serializable
data class CommentsPage(
    var comments: List<Comment> = emptyList(),
    val disabled: Boolean = false,
    val nextpage: String? = null,
    val commentCount: Long = 0
)
