package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.audioplayer.domain.Track

class SearchHistory (sharedPreferences: SharedPreferences) {

    private val sharedPrefs: SharedPreferences

    init {
        sharedPrefs = sharedPreferences
    }

    fun get(): Array<Track>{
        val saveJson = sharedPrefs.getString(KEY_LIST_TRACKS, "[ ]")
        return Gson().fromJson(saveJson, Array<Track>::class.java)
    }

    fun add(track: Track) {
        val listObjects = get().filter { it.trackId != track.trackId }.toMutableList()
        if ( listObjects.size >= 10 ) {
            listObjects.removeLast()
        }
        listObjects.add(0, track)
        val json = Gson().toJson(listObjects)
        sharedPrefs.edit()
            .putString(KEY_LIST_TRACKS, json)
            .apply()
    }

    companion object {
        const val KEY_LIST_TRACKS = "tracks_history"
    }
}