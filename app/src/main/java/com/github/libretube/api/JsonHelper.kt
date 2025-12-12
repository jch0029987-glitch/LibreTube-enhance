package dev.jch0029987.libretibs.api

import kotlinx.serialization.json.Json

object JsonHelper {
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
}
