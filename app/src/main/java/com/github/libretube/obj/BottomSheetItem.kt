package dev.jch0029987.libretibs.obj

data class BottomSheetItem(
    var title: String,
    val drawable: Int? = null,
    val getCurrent: () -> String? = { null },
    val onClick: () -> Unit = {}
)
