package dev.jch0029987.libretibs.debug

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import androidx.fragment.app.FragmentActivity
import dev.jch0029987.libretibs.ui.fragments.PlayerFragment
import com.google.android.exoplayer2.ExoPlayer

class DebugServer(private val context: Context, port: Int = 8080) : NanoHTTPD(port) {

    private val playerFragment: PlayerFragment?
        get() = (context as? FragmentActivity)
            ?.supportFragmentManager
            ?.fragments
            ?.firstOrNull { it is PlayerFragment } as? PlayerFragment

    fun getCurrentVideoId(): String? {
        return playerFragment?.getCurrentVideoId()
    }

    fun isPlaying(): Boolean {
        return playerFragment?.isPlaying() ?: false
    }

    fun getPlayer(): ExoPlayer? {
        return playerFragment?.getPlayer()
    }

    override fun serve(session: IHTTPSession?): Response {
        val id = getCurrentVideoId() ?: "unknown"
        val playing = isPlaying()
        val html = """
            <html>
                <body>
                    <h2>LibreTube DebugServer</h2>
                    <p>Current Video ID: $id</p>
                    <p>Is Playing: $playing</p>
                </body>
            </html>
        """.trimIndent()

        return newFixedLengthResponse(Response.Status.OK, "text/html", html)
    }

    fun logStatus() {
        println("DebugServer: currentVideoId=${getCurrentVideoId()}, isPlaying=${isPlaying()}")
    }
}
