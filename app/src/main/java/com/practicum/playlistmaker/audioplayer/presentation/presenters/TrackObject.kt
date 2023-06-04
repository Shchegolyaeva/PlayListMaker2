package com.practicum.playlistmaker.audioplayer.presentation.presenters

import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.AudioPlayerActivity
import com.practicum.playlistmaker.audioplayer.domain.Track

object TrackObject {
    fun getTrack(activity: AppCompatActivity): Track {
        return Gson().fromJson((activity.intent.getStringExtra(AudioPlayerActivity.TRACK_OBJECT)),
            Track::class.java)
    }
}