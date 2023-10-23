package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.audioplayer.presentation.presenters.AudioPlayerPresenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.audioplayer.domain.Track
import com.practicum.playlistmaker.audioplayer.presentation.presenters.AudioPlayerView
import com.practicum.playlistmaker.audioplayer.presentation.presenters.TrackObject
import com.practicum.playlistmaker.data.audioplayer.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.domain.audioplayer.impl.MediaInteractorImpl
import java.text.SimpleDateFormat
import java.util.*

class AudioPlayerActivity : AppCompatActivity(), AudioPlayerView {

    companion object {
        const val TRACK_OBJECT = "track_object"
    }

    private lateinit var coverImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var albumName: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var addButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var url: String
    private lateinit var returnItemImageView: androidx.appcompat.widget.Toolbar
    private lateinit var excerptDuration: TextView
    private lateinit var presenter: AudioPlayerPresenter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        settingTrack()
        settingListeners()
        url = TrackObject.getTrack(this).previewUrl
        presenter = AudioPlayerPresenter(
            activity = this,
            view = this,
            mediaInteractor = MediaInteractorImpl(AudioPlayerRepositoryImpl(url))
        )
    }

    override fun onPause() {
        super.onPause()
        presenter.pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    // Достает трек из json и отображает его
    private fun settingTrack() {
        returnItemImageView = findViewById(R.id.back_icon)
        coverImage = findViewById(R.id.cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        duration = findViewById(R.id.changeable_duration)
        albumName = findViewById(R.id.changeable_album)
        year = findViewById(R.id.changeable_year)
        genre = findViewById(R.id.changeable_genre)
        country = findViewById(R.id.changeable_country)
        addButton = findViewById(R.id.add_button)
        playButton = findViewById(R.id.play_button)
        likeButton = findViewById(R.id.like_button)
        excerptDuration = findViewById(R.id.excerpt_duration)
    }

    //cлушатель кнопки назад и кнопки play
    private fun settingListeners() {
        returnItemImageView.setNavigationOnClickListener {
            presenter.backButtonClicked()
        }

        playButton.setOnClickListener {
            playButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation))
            presenter.playButtonClicked()
        }
    }

    override fun createTrack(track: Track, startPosition: Int) {

        url = track.previewUrl

        updateTrackDuration(currentPositionMediaPlayer = startPosition)
        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        albumName.text = track.collectionName

        year.text = track.releaseDate.substring(0, 4)
        genre.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.ic_vector)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(coverImage)
    }

    override fun updateTrackDuration(currentPositionMediaPlayer: Int) {
        excerptDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPositionMediaPlayer)
    }

    override fun updatePlayButton(imageResource: Int) {
        playButton.setImageResource(imageResource)
    }
}