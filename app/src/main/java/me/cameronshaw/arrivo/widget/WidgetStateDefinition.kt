package me.cameronshaw.arrivo.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.glance.state.GlanceStateDefinition
import java.io.File

class WidgetStateDefinition(
    private val serializer: Serializer<WidgetState>,
    private val fileName: String
) : GlanceStateDefinition<WidgetState> {
    companion object {
        private val dataStores = mutableMapOf<String, DataStore<WidgetState>>()
    }

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<WidgetState> {
        val path = getLocation(context, fileKey).absolutePath

        return synchronized(dataStores) {
            dataStores.getOrPut(path) {
                DataStoreFactory.create(
                    serializer = serializer,
                    produceFile = { File(path) }
                )
            }
        }
    }

    override fun getLocation(
        context: Context,
        fileKey: String
    ): File {
        return File(context.filesDir, fileName)
    }

}