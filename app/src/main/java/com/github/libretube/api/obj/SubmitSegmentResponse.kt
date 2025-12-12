package dev.jch0029987.libretibs.api.obj

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitSegmentResponse(
    @SerialName("UUID") val uuid: String,
    val category: String,
    val segment: List<Float>
)
