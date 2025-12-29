package dev.jch0029987.libretibs.debug

object DebugBridge {

    @JvmStatic
    fun loaded(): Boolean {
        return true
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
