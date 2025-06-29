package me.cameronshaw.amtraker.data.remote.api

object TestUtils {
    fun getJson(path: String): String {
        val uri = this.javaClass.classLoader!!.getResource(path)
        val file = java.io.File(uri.path)
        return String(file.readBytes())
    }
}