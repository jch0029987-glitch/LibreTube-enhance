package dev.jch0029987.libretibs.debug

object DebugBridge {

    init {
        runCatching {
            System.loadLibrary("frida-gadget")
        }
    }

    @JvmStatic
    fun ping(msg: String) {
        android.util.Log.d("DEBUG_BRIDGE", msg)
    }
}
