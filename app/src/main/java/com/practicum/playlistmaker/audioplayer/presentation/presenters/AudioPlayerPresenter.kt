package com.practicum.playlistmaker.audioplayer.presentation.presenters

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.audioplayer.domain.StatePlayer
import com.practicum.playlistmaker.domain.audioplayer.api.MediaInteractor


class AudioPlayerPresenter(
    private val activity: AppCompatActivity,
    private val view: AudioPlayerView,
    private val mediaInteractor: MediaInteractor
) {
    init {
        view.createTrack(track = TrackObject.getTrack(activity),
            startPosition = START_POSITION)
    }
    companion object {
        private const val DELAY = 1000L
        private const val START_POSITION = 0
    }
    private val handler = Handler(Looper.getMainLooper())

    fun backButtonClicked() {
        activity.finish()
    }

    fun playButtonClicked() {
        when(mediaInteractor.getPlayerState()) {
            StatePlayer.PLAYING -> pausePlayer()
            else -> startPlayer()
        }
    }

    private fun startPlayer() {
        view.updatePlayButton(R.drawable.stop_button)
        mediaInteractor.startPlaying()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (mediaInteractor.getPlayerState() == StatePlayer.READY) {
                    view.updateTrackDuration(currentPositionMediaPlayer = 0)
                    view.updatePlayButton(imageResource = R.drawable.play_button)
                    handler.removeCallbacksAndMessages(null)
                } else {
                    view.updateTrackDuration(currentPositionMediaPlayer = mediaInteractor.getPlayerPosition())
                    handler.postDelayed(this, DELAY)
                }
            }
        }, DELAY)
    }

    fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        mediaInteractor.stopPlaying()
    }

    fun pausePlayer() {
        mediaInteractor.pausePlaying()
        view.updatePlayButton(R.drawable.play_button)
        handler.removeCallbacksAndMessages(null)
    }
}