package dev.jch0029987.libretibs.debug

import android.util.Log
import dev.jch0029987.libretibs.ui.activities.MainActivity
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import fi.iki.elonen.NanoHTTPD.Response
import fi.iki.elonen.NanoHTTPD.newFixedLengthResponse

class DebugServer(
    private val activity: MainActivity
) : NanoHTTPD(8765) {

    override fun serve(session: IHTTPSession): Response {
        return when (session.uri) {
            "/status" -> serveStatus()
            else -> newFixedLengthResponse(
                Response.Status.NOT_FOUND,
                MIME_PLAINTEXT,
                "Not found"
            )
        }
    }

    private fun serveStatus(): Response {
        val searchQuery = activity.getCurrentSearchQuery()

        var videoId: String? = null
        var isPlaying = false

        activity.runOnPlayerFragment {
            videoId = getCurrentVideoId()
            isPlaying = isPlaying()
            true
        }

        val json = """
            {
              "searchQuery": ${searchQuery?.let { "\"$it\"" } ?: "null"},
              "videoId": ${videoId?.let { "\"$it\"" } ?: "null"},
              "isPlaying": $isPlaying
            }
        """.trimIndent()

        return newFixedLengthResponse(
            Response.Status.OK,
            "application/json",
            json
        )
    }

    fun startServer() {
        try {
            start(SOCKET_READ_TIMEOUT, false)
            Log.d("DebugServer", "Debug server started on port 8765")
        } catch (e: Exception) {
            Log.e("DebugServer", "Failed to start debug server", e)
        }
    }
}
