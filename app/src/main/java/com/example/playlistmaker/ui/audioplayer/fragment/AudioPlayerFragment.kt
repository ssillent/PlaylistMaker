package com.example.playlistmaker.ui.audioplayer.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.audioplayer.view_model.PlayerState
import com.example.playlistmaker.ui.search.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<AudioPlayerViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupObservers()

        val track = getTrackFromArguments()
        track?.let {
            displayTrackData(it)
            viewModel.preparePlayer(it.previewUrl)
        }
    }


    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.AudioPlayerBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.PlayButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updatePlayButton(state.playerState)
            binding.TrackTime.text = state.progressTime
        }
    }

    private fun updatePlayButton(state: PlayerState) {
        when (state) {
            PlayerState.DEFAULT -> {
                binding.PlayButton.isEnabled = false
                binding.PlayButton.setImageResource(R.drawable.play_button)
            }
            PlayerState.PREPARED,
            PlayerState.PAUSED -> {
                binding.PlayButton.isEnabled = true
                binding.PlayButton.setImageResource(R.drawable.play_button)
            }
            PlayerState.PLAYING -> {
                binding.PlayButton.isEnabled = true
                binding.PlayButton.setImageResource(R.drawable.pause)
            }
        }
    }


    private fun getTrackFromArguments(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("track") as? Track
        }
    }

    private fun displayTrackData(track: Track) {
        binding.trackTitle.text = track.trackName
        binding.artistName.text = track.artistName
        binding.durationValue.text = track.trackTimeMillis
        binding.TrackTime.text = "00:00"

        loadTrackImage(track.getUpdatedArtwork())
        setOptionalValues(track)
    }

    private fun loadTrackImage(updatedUrl: String) {
        val pxSize = dpToPx(8f, requireContext())

        Glide.with(this)
            .load(updatedUrl)
            .centerCrop()
            .transform(RoundedCorners(pxSize))
            .placeholder(R.drawable.audioplayer_place_holder)
            .into(binding.trackImage)
    }

    private fun setOptionalValues(track: Track) {

        if (!track.collectionName.isNullOrEmpty()) {
            binding.albumValue.text = track.collectionName
        } else {
            binding.album.isVisible = false
            binding.albumValue.isVisible = false
        }

        val releaseYear = track.getReleaseYear()
        if (!releaseYear.isNullOrEmpty()) {
            binding.yearValue.text = releaseYear
        } else {
            binding.year.isVisible = false
            binding.yearValue.isVisible = false
        }

        if (track.country.isNotEmpty()) {
            binding.countryValue.text = track.country
        } else {
            binding.country.isVisible = false
            binding.countryValue.isVisible = false
        }

        if (track.primaryGenreName.isNotEmpty()) {
            binding.genreValue.text = track.primaryGenreName
        } else {
            binding.genre.isVisible = false
            binding.genreValue.isVisible = false
        }
    }



}