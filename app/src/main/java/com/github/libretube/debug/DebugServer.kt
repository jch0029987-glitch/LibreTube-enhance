package dev.jch0029987.libretibs.debug

import fi.iki.elonen.NanoHTTPD

object DebugServer : NanoHTTPD("127.0.0.1", 8765) {

    fun startServer() {
        try {
            start(SOCKET_READ_TIMEOUT, false)
            DebugBridge.log("DebugServer started on 127.0.0.1:8765")
        } catch (e: Exception) {
            DebugBridge.log("DebugServer failed: ${e.message}")
        }
    }

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        val params = session.parameters

        when (uri) {
            "/toast" -> {
                val msg = params["msg"]?.firstOrNull() ?: "Hello"
                DebugBridge.toast(msg)
                return ok("toast sent")
            }

            "/log" -> {
                val msg = params["msg"]?.firstOrNull() ?: "log"
                DebugBridge.log(msg)
                return ok("log sent")
            }

            "/ping" -> {
                return ok("pong")
            }

            else -> {
                return notFound()
            }
        }
    }

    private fun ok(msg: String) =
        newFixedLengthResponse(Response.Status.OK, "text/plain", msg)

    private fun notFound() =
        newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404")
}
