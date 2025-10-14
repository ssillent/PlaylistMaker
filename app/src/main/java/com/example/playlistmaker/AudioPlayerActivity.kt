package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
    }

    private lateinit var AudioPlayerbackButton: ImageButton
    private lateinit var trackImage: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

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


        AudioPlayerbackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        displayTrackData()
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
            trackTime.text = "0:00"
            durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(track.trackTimeMillis))
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(track.trackTimeMillis))

            loadTrackImage(track.getUpdatedArtwork())

            setOptionalValues(track)
        }
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