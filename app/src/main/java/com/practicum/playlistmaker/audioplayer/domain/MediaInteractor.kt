package com.practicum.playlistmaker.domain.audioplayer.api

import com.practicum.playlistmaker.audioplayer.domain.StatePlayer

interface MediaInteractor {
    fun startPlaying()
    fun pausePlaying()
    fun stopPlaying()
    fun getPlayerPosition(): Int
    fun getPlayerState(): StatePlayer
}