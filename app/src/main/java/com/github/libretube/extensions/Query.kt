package dev.jch0029987.libretibs.extensions

fun query(block: () -> Unit) {
    Thread {
        try {
            block.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.start()
}
