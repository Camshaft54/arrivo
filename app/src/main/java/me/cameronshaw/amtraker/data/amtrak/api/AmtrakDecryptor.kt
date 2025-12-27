package me.cameronshaw.amtraker.data.amtrak.api

import me.cameronshaw.amtraker.data.amtrak.dto.RoutesListEntry
import me.cameronshaw.amtraker.data.amtrak.dto.RoutesListValuesDto
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64

const val MASTER_SEGMENT = 88

@Singleton
class AmtrakDecryptor @Inject constructor() {
    private var cryptoInitializers: AmtrakCryptoInitializers? = null

    fun updateKeys(routesList: Array<RoutesListEntry>, routesListValues: RoutesListValuesDto) {
        val masterZoom = routesList.sumOf { it.zoomLevel }
        cryptoInitializers = AmtrakCryptoInitializers(
            publicKey = routesListValues.publicKeys[masterZoom],
            cryptoSalt = routesListValues.salts[routesListValues.salts[0].length],
            cryptoIV = routesListValues.ivs[routesListValues.ivs[0].length]
        )

    }

    fun hasKeys(): Boolean = cryptoInitializers != null

    private fun decryptRaw(data: String, keyDerivationPassword: String?): String {
        val keys = cryptoInitializers ?: throw IllegalStateException("Keys not initialized")
        val binaryCiphertext = Base64.decode(data)

        val spec = PBEKeySpec(
            keyDerivationPassword?.toCharArray() ?: keys.publicKey.toCharArray(),
            keys.cryptoSalt.hexToByteArray(),
            1000,
            128
        )

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val key = factory.generateSecret(spec).encoded

        val aesKey = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(keys.cryptoIV.hexToByteArray())

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec)

        val plaintext = cipher.doFinal(binaryCiphertext)
        return String(plaintext, StandardCharsets.UTF_8)
    }

    fun decrypt(encryptedData: String): String {
        val ciphertext = encryptedData.dropLast(MASTER_SEGMENT)
        val privateKeyCipher = encryptedData.takeLast(MASTER_SEGMENT)

        val privateKey = decryptRaw(privateKeyCipher, null).takeWhile { it != '|' }
        val plaintext = decryptRaw(ciphertext, privateKey)

        return plaintext
    }
}

data class AmtrakCryptoInitializers(
    val publicKey: String,
    val cryptoSalt: String,
    val cryptoIV: String
)

private fun String.hexToByteArray(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}
