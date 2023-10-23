package com.practicum.playlistmaker.audioplayer.presentation.presenters

import com.practicum.playlistmaker.audioplayer.domain.Track

interface AudioPlayerView {
    fun createTrack(track: Track, startPosition: Int)
    fun updatePlayButton(imageResource: Int)
    fun updateTrackDuration(currentPositionMediaPlayer: Int)
}