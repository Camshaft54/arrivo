package me.cameronshaw.amtraker.widget

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
    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<WidgetState> {
        return DataStoreFactory.create(
            serializer = serializer,
            produceFile = { getLocation(context, fileKey) }
        )
    }

    override fun getLocation(
        context: Context,
        fileKey: String
    ): File {
        return File(context.filesDir, fileName)
    }

}