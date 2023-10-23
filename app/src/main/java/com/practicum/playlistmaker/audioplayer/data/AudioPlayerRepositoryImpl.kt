package com.practicum.playlistmaker.data.audioplayer

import android.media.MediaPlayer
import com.practicum.playlistmaker.audioplayer.domain.AudioPlayerRepository
import com.practicum.playlistmaker.audioplayer.domain.StatePlayer

class AudioPlayerRepositoryImpl(url: String) : AudioPlayerRepository {

    override var statePlayer = StatePlayer.NOT_PREPARED
    private val player = MediaPlayer()

    init {
        player.apply {
            setDataSource(url)
            setOnCompletionListener {
                statePlayer = StatePlayer.READY
            }
        }
    }

    override fun getCurrentPosition(): Int {
        return player.currentPosition
    }

    override fun startPlayer() {
        if (statePlayer == StatePlayer.NOT_PREPARED){
            player.prepare()
            statePlayer = StatePlayer.READY
        }
        player.start()
        statePlayer = StatePlayer.PLAYING

    }

    override fun pausePlayer() {
        player.pause()
        statePlayer = StatePlayer.PAUSED
    }

    override fun release() {
        player.release()
    }
}