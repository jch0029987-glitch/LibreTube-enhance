package dev.jch0029987.libretibs.debug

import android.util.Log
import dev.jch0029987.libretibs.BuildConfig
import dev.jch0029987.libretibs.ui.activities.MainActivity
import dev.jch0029987.libretibs.ui.fragments.PlayerFragment
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

object DebugServer {

    private const val PORT = 8080
    private var server: NanoHttpServer? = null

    var mainActivity: MainActivity? = null

    /**
     * Starts the debug server if in a debug build.
     * @param activity Pass the MainActivity to access app state.
     */
    fun startIfDebug(activity: MainActivity) {
        if (!BuildConfig.DEBUG) return
        if (server != null) return

        mainActivity = activity
        server = NanoHttpServer(PORT)
        try {
            server?.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
            Log.d("DebugServer", "Debug server started on port $PORT")
        } catch (e: IOException) {
            Log.e("DebugServer", "Failed to start debug server", e)
            server = null
        }
    }

    fun stopServer() {
        server?.stop()
        server = null
        Log.d("DebugServer", "Debug server stopped")
    }

    private class NanoHttpServer(port: Int) : NanoHTTPD(port) {
        override fun serve(session: IHTTPSession?): Response {
            val uri = session?.uri ?: "/"
            Log.d("DebugServer", "Received request: $uri")

            return when (uri) {
                "/status" -> newFixedLengthResponse(
                    Response.Status.OK,
                    "application/json",
                    """{"status":"running"}"""
                )

                "/player" -> {
                    val playerInfo = getPlayerInfo()
                    newFixedLengthResponse(
                        Response.Status.OK,
                        "application/json",
                        playerInfo
                    )
                }

                "/search" -> {
                    val query = mainActivity?.mainActivity?.getCurrentSearchQuery() ?: ""
                    newFixedLengthResponse(
                        Response.Status.OK,
                        "application/json",
                        """{"currentSearch":"$query"}"""
                    )
                }

                else -> newFixedLengthResponse(
                    Response.Status.NOT_FOUND,
                    "text/plain",
                    "Endpoint not found: $uri"
                )
            }
        }

    private fun getPlayerInfo(): String {
        val activity = mainActivity ?: return """{"player":"not available"}"""
        var info = """{"player":"none"}"""
        activity.runOnPlayerFragment {
            val videoId = getCurrentVideoId()
            val playing = isPlaying()
            info = """{"player":"active","videoId":"$videoId","isPlaying":$playing}"""
            true
        }
        return info
    }
        }
    }
}
