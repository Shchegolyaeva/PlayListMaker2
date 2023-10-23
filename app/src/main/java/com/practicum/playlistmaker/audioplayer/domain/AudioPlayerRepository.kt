package com.practicum.playlistmaker.audioplayer.domain

import com.practicum.playlistmaker.audioplayer.domain.StatePlayer

interface AudioPlayerRepository {

    var statePlayer: StatePlayer
    fun getCurrentPosition(): Int
    fun startPlayer()
    fun pausePlayer()
    fun release()
}