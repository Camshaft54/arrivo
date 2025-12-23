package me.cameronshaw.amtraker.data.amtrakremote.api

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import javax.inject.Inject

class AmtrakDecryptionInterceptor @Inject constructor(
    private val decryptor: AmtrakDecryptor
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val needsDecryption = request.header("X-Requires-Decryption") == "true"

        if (response.isSuccessful && needsDecryption) {
            if (!decryptor.hasKeys()) {
                throw IOException("Decryption keys missing. Perform handshake first.")
            }

            val bodyString = response.body?.string() ?: ""
            val decryptedJson = decryptor.decrypt(bodyString)

            return response.newBuilder()
                .body(decryptedJson.toResponseBody(response.body?.contentType()))
                .build()
        }

        return response
    }
}