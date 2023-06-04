package com.practicum.playlistmaker.domain.audioplayer.api

import com.practicum.playlistmaker.audioplayer.domain.StatePlayer

interface AudioPlayer {

    var statePlayer: StatePlayer
    fun getCurrentPosition(): Int
    fun startPlayer()
    fun pausePlayer()
    fun release()
}