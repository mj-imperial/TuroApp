package com.example.turomobileapp.objects

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object CelebrationPrefs {
    val Context.dataStore by preferencesDataStore(name = "celebration_prefs")

    private val CELEBRATED_BADGES_KEY = stringSetPreferencesKey("celebrated_badge_ids")

    private val CELEBRATED_ACHIEVEMENTS_KEY = stringSetPreferencesKey("celebrated_achievement_ids")

    suspend fun getCelebratedBadges(context: Context): Set<String> {
        val prefs = context.dataStore.data.first()
        return prefs[CELEBRATED_BADGES_KEY] ?: emptySet()
    }

    suspend fun getCelebratedAchievements(context: Context): Set<String>{
        val prefs = context.dataStore.data.first()
        return prefs[CELEBRATED_ACHIEVEMENTS_KEY] ?: emptySet()
    }

    suspend fun appendCelebratedBadges(context: Context, newIds: Set<String>) {
        context.dataStore.edit { prefs ->
            val current = prefs[CELEBRATED_BADGES_KEY]?.toMutableSet() ?: mutableSetOf()
            current.addAll(newIds)
            prefs[CELEBRATED_BADGES_KEY] = current
        }
    }

    suspend fun appendCelebratedAchievements(context: Context, newIds: Set<String>){
        context.dataStore.edit { prefs ->
            val current = prefs[CELEBRATED_ACHIEVEMENTS_KEY]?.toMutableSet() ?: mutableSetOf()
            current.addAll(newIds)
            prefs[CELEBRATED_ACHIEVEMENTS_KEY] = current
        }
    }
}