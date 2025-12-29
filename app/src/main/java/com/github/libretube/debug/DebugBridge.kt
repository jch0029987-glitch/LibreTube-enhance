package dev.jch0029987.libretibs.debug

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object DebugBridge {

    private lateinit var appContext: Context
    private val mainHandler = Handler(Looper.getMainLooper())

    @JvmStatic
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    @JvmStatic
    fun toast(message: String) {
        if (!::appContext.isInitialized) return

        mainHandler.post {
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    @JvmStatic
    fun echo(input: String): String {
        return "DebugBridge: $input"
    }

    @JvmStatic
    fun add(a: Int, b: Int): Int {
        return a + b
    }
}
