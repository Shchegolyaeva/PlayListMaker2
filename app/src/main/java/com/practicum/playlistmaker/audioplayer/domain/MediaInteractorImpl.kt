package com.practicum.playlistmaker.domain.audioplayer.impl

import com.practicum.playlistmaker.audioplayer.domain.StatePlayer
import com.practicum.playlistmaker.audioplayer.domain.AudioPlayerRepository
import com.practicum.playlistmaker.domain.audioplayer.api.MediaInteractor


class MediaInteractorImpl(
    private val player: AudioPlayerRepository
) : MediaInteractor {

    override fun startPlaying() {
        player.startPlayer()
    }

    override fun pausePlaying() {
        player.pausePlayer()
    }

    override fun stopPlaying() {
        player.release()
    }

    override fun getPlayerPosition(): Int {
        return player.getCurrentPosition()
    }

    override fun getPlayerState(): StatePlayer {
        return player.statePlayer
    }
}