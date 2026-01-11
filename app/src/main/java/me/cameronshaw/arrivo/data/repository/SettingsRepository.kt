package me.cameronshaw.arrivo.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.cameronshaw.arrivo.data.local.model.AppSettings
import me.cameronshaw.arrivo.data.util.NEVER
import me.cameronshaw.arrivo.data.util.toDbString
import me.cameronshaw.arrivo.data.util.toOffsetDateTime
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

interface SettingsRepository {
    fun appSettingsFlow(): Flow<AppSettings>
    suspend fun updateTheme(theme: String)

    suspend fun updateProvider(provider: String)

    suspend fun updateTrainsLastUpdated(time: OffsetDateTime)
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(private val context: Context) :
    SettingsRepository {

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val DATA_PROVIDER = stringPreferencesKey("data_provider")
        val TRAINS_LAST_UPDATED = stringPreferencesKey("trains_last_updated")
    }

    override fun appSettingsFlow(): Flow<AppSettings> = context.settingsDataStore.data
        .map { preferences ->
            mapAppSettings(preferences)
        }

    override suspend fun updateTheme(theme: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme
        }
    }

    override suspend fun updateProvider(provider: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.DATA_PROVIDER] = provider
        }
    }

    override suspend fun updateTrainsLastUpdated(time: OffsetDateTime) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.TRAINS_LAST_UPDATED] = time.toDbString()
        }
    }

    private fun mapAppSettings(preferences: Preferences): AppSettings {
        val theme = preferences[PreferencesKeys.APP_THEME] ?: "SYSTEM"
        val provider = preferences[PreferencesKeys.DATA_PROVIDER] ?: "AMTRAK"
        val trainsLastUpdated = preferences[PreferencesKeys.TRAINS_LAST_UPDATED] ?: ""
        return AppSettings(
            theme = theme,
            dataProvider = provider,
            trainsLastUpdated = trainsLastUpdated.toOffsetDateTime() ?: NEVER
        )
    }
}