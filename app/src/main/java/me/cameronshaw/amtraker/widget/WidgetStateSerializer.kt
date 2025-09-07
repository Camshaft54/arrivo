package me.cameronshaw.amtraker.widget

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.time.OffsetDateTime

object WidgetStateSerializer : Serializer<WidgetState> {
    override val defaultValue: WidgetState = WidgetState(
        lastUpdated = OffsetDateTime.now(),
        schedule = emptyList()
    )

    override suspend fun readFrom(input: InputStream): WidgetState {
        return try {
            Json.decodeFromString(
                deserializer = WidgetState.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            Log.e("WidgetStateSerializer", exception.toString())
            defaultValue
        }
    }

    override suspend fun writeTo(t: WidgetState, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = WidgetState.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}