package dev.jch0029987.libretibs.ui.extensions

import dev.jch0029987.libretibs.api.obj.Comment

fun List<Comment>.filterNonEmptyComments(): List<Comment> {
    return filter { !it.commentText.isNullOrEmpty() }
}
