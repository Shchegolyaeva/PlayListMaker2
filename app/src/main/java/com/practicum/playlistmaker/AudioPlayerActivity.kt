package com.practicum.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_OBJECT = "track_object"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 1000L
    }

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

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


    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        settingTrack()
        preparePlayer()
        settingListeners()

    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
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

       val track = Gson().fromJson((intent.getStringExtra(TRACK_OBJECT)), Track::class.java)

        url = track.previewUrl

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
    //cлушатель кнопки назад и кнопки play
    private fun settingListeners() {
        returnItemImageView.setNavigationOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation))
            playbackControl()

        }
    }
    //подготавливает медиаплеер к воспроизведению
    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            handler.removeCallbacksAndMessages(null)
            changeDuration(0)
            playerState = STATE_PREPARED
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playButton.setImageResource(R.drawable.stop_button)
        handler.postDelayed(object : Runnable {
            override fun run() {
                changeDuration(mediaPlayer.currentPosition)
                handler.postDelayed(this, DELAY)
            }
        }, DELAY)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playButton.setImageResource(R.drawable.play_button)
        handler.removeCallbacksAndMessages(null)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    //меняем время по ходу воспроизведения
    private fun changeDuration(milliseconds: Int) {
        excerptDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(milliseconds)
    }
}