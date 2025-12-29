package dev.jch0029987.libretibs.debug

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

object DebugBridge {

    private lateinit var appContext: Context
    private val mainHandler = Handler(Looper.getMainLooper())

    fun init(context: Context) {
        appContext = context.applicationContext
        log("DebugBridge initialized")
    }

    fun toast(message: String) {
        mainHandler.post {
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun log(message: String) {
        Log.d("LibreTube-Debug", message)
    }
}
