package com.example.playlistmaker.presentation.ui.audioplayer

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.search.dpToPx
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity(){

    companion object {
        private const val TRACK_EXTRA = "track_extra"

        fun getIntent(context: Context, track: Track): Intent {
            return Intent(context, AudioPlayerActivity::class.java).apply {
                putExtra(TRACK_EXTRA, track)
            }
        }

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private var previewUrl = ""

    private lateinit var AudioPlayerbackButton: ImageButton
    private lateinit var AudioPlayerPlayButton: ImageButton
    private lateinit var trackImage: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())

    private val updateTrackRunnable = object : Runnable {
        override fun run() {
            updateTrackProgress()
            if (playerState == STATE_PLAYING) {
                handler.postDelayed(this, 300L)            }
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        enableEdgeToEdge()

        AudioPlayerbackButton = findViewById(R.id.AudioPlayerBackButton)
        trackImage = findViewById(R.id.trackImage)
        trackTitle = findViewById(R.id.trackTitle)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.TrackTime)
        durationValue = findViewById(R.id.durationValue)
        albumValue = findViewById(R.id.albumValue)
        yearValue = findViewById(R.id.yearValue)
        genreValue = findViewById(R.id.genreValue)
        countryValue = findViewById(R.id.countryValue)
        AudioPlayerPlayButton = findViewById(R.id.PlayButton)


        AudioPlayerbackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        AudioPlayerPlayButton.isEnabled = false

        displayTrackData()

        AudioPlayerPlayButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun displayTrackData(){
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(TRACK_EXTRA, Track::class.java)
        } else {
            intent.getSerializableExtra(TRACK_EXTRA) as? Track
        }

        if (track != null) {
            trackTitle.text = track.trackName
            artistName.text = track.artistName
            durationValue.text = track.trackTimeMillis
            trackTime.text = track.trackTimeMillis

            previewUrl = track.previewUrl

            mediaPlayer.reset()
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                AudioPlayerPlayButton.isEnabled = true
                playerState = STATE_PREPARED
                updatePlayIcon()
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                stopUpdateTrackProgress()
                updatePlayIcon()
                trackTime.text = "0:00"
            }

            loadTrackImage(track.getUpdatedArtwork())

            setOptionalValues(track)
        }
    }

    private fun updatePlayIcon() {
        val changeIcon = when {
            mediaPlayer.isPlaying -> R.drawable.pause
            else -> R.drawable.play_button
        }
        AudioPlayerPlayButton.setImageResource(changeIcon)
    }

    private fun updateTrackProgress() {
        if (playerState == STATE_PLAYING) {
           trackTime.text = SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        }
    }


    private fun startUpdateTrackProgress() {
        handler.post(updateTrackRunnable)
    }

    private fun stopUpdateTrackProgress() {
        handler.removeCallbacks(updateTrackRunnable)
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        updatePlayIcon()
        startUpdateTrackProgress()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        updatePlayIcon()
        stopUpdateTrackProgress()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    override fun onPause() {
        super.onPause()

        if(playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdateTrackProgress()
        mediaPlayer.release()
    }

    private fun loadTrackImage(updatedUrl: String){
        val pxSize = dpToPx(8f, this)

        Glide.with(this)
            .load(updatedUrl)
            .centerCrop()
            .transform(RoundedCorners(pxSize))
            .placeholder(R.drawable.audioplayer_place_holder)
            .into(trackImage)
    }

    private fun setOptionalValues(track: Track){

        if (!track.collectionName.isNullOrEmpty()){
            albumValue.text = track.collectionName
        } else {
            findViewById<TextView>(R.id.album).visibility = View.GONE
            albumValue.visibility = View.GONE
        }

        val releaseYear = track.getReleaseYear()
        if (!releaseYear.isNullOrEmpty()){
            yearValue.text = releaseYear
        } else {
            findViewById<TextView>(R.id.year).visibility = View.GONE
            yearValue.visibility = View.GONE
        }

        if (track.country.isNotEmpty()){
            countryValue.text = track.country
        } else {
            findViewById<TextView>(R.id.country).visibility = View.GONE
            countryValue.visibility = View.GONE
        }

        if (track.primaryGenreName.isNotEmpty()){
            genreValue.text = track.primaryGenreName
        } else {
            findViewById<TextView>(R.id.genre).visibility = View.GONE
            genreValue.visibility = View.GONE
        }

    }



}