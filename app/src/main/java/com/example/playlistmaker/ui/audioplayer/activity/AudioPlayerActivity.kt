package com.example.playlistmaker.ui.audioplayer.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.search.dpToPx

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val TRACK_EXTRA = "track_extra"

        fun getIntent(context: Context, track: Track): Intent {
            return Intent(context, AudioPlayerActivity::class.java).apply {
                putExtra(TRACK_EXTRA, track)
            }
        }
    }

    private lateinit var audioPlayerBackButton: ImageButton
    private lateinit var audioPlayerPlayButton: ImageButton
    private lateinit var trackImage: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView

    private val viewModel: AudioPlayerViewModel by viewModels {
        AudioPlayerViewModel.getFactory()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        enableEdgeToEdge()

        initViews()
        setupClickListeners()
        setupObservers()

        val track = getTrackFromIntent()
        track?.let {
            displayTrackData(it)


            val previewUrl = it.previewUrl
            viewModel.preparePlayer(previewUrl)
        }
    }

    private fun initViews() {
        audioPlayerBackButton = findViewById(R.id.AudioPlayerBackButton)
        trackImage = findViewById(R.id.trackImage)
        trackTitle = findViewById(R.id.trackTitle)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.TrackTime)
        durationValue = findViewById(R.id.durationValue)
        albumValue = findViewById(R.id.albumValue)
        yearValue = findViewById(R.id.yearValue)
        genreValue = findViewById(R.id.genreValue)
        countryValue = findViewById(R.id.countryValue)
        audioPlayerPlayButton = findViewById(R.id.PlayButton)
    }

    private fun setupClickListeners() {
        audioPlayerBackButton.setOnClickListener {
            finish()
        }

        audioPlayerPlayButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    private fun setupObservers() {
        viewModel.playerState.observe(this) { state ->
            updatePlayButton(state)
        }

        viewModel.progressTime.observe(this) { progress ->
            trackTime.text = progress
        }
    }

    private fun updatePlayButton(state: Int) {
        when (state) {
            AudioPlayerViewModel.STATE_DEFAULT -> {
                audioPlayerPlayButton.isEnabled = false
                audioPlayerPlayButton.setImageResource(R.drawable.play_button)
            }
            AudioPlayerViewModel.STATE_PREPARED,
            AudioPlayerViewModel.STATE_PAUSED -> {
                audioPlayerPlayButton.isEnabled = true
                audioPlayerPlayButton.setImageResource(R.drawable.play_button)
            }
            AudioPlayerViewModel.STATE_PLAYING -> {
                audioPlayerPlayButton.isEnabled = true
                audioPlayerPlayButton.setImageResource(R.drawable.pause)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun getTrackFromIntent(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(TRACK_EXTRA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(TRACK_EXTRA) as? Track
        }
    }

    private fun displayTrackData(track: Track) {
        trackTitle.text = track.trackName
        artistName.text = track.artistName
        durationValue.text = track.trackTimeMillis
        trackTime.text = "00:00"

        loadTrackImage(track.getUpdatedArtwork())
        setOptionalValues(track)
    }

    private fun loadTrackImage(updatedUrl: String) {
        val pxSize = dpToPx(8f, this)

        Glide.with(this)
            .load(updatedUrl)
            .centerCrop()
            .transform(RoundedCorners(pxSize))
            .placeholder(R.drawable.audioplayer_place_holder)
            .into(trackImage)
    }

    private fun setOptionalValues(track: Track) {
        if (!track.collectionName.isNullOrEmpty()) {
            albumValue.text = track.collectionName
        } else {
            findViewById<TextView>(R.id.album).isVisible = false
            albumValue.isVisible = false
        }


        val releaseYear = track.getReleaseYear()
        if (!releaseYear.isNullOrEmpty()) {
            yearValue.text = releaseYear
        } else {
            findViewById<TextView>(R.id.year).isVisible = false
            yearValue.isVisible = false
        }


        if (track.country.isNotEmpty()) {
            countryValue.text = track.country
        } else {
            findViewById<TextView>(R.id.country).isVisible = false
            countryValue.isVisible = false
        }


        if (track.primaryGenreName.isNotEmpty()) {
            genreValue.text = track.primaryGenreName
        } else {
            findViewById<TextView>(R.id.genre).isVisible = false
            genreValue.isVisible = false
        }
    }
}