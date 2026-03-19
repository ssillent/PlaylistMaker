package com.example.playlistmaker.ui.audioplayer.fragment

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.CustomToastBinding
import com.example.playlistmaker.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioplayer.AddToPlaylistAdapter
import com.example.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.audioplayer.view_model.PlayerState
import com.example.playlistmaker.ui.search.dpToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<AudioPlayerViewModel>()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var adapter: AddToPlaylistAdapter


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

        setupBottomSheet()
        setupRecyclerview()
        setupClickListeners()
        setupObservers()

        val track = getTrackFromArguments()
        track?.let {
            viewModel.setTrack(it)
            displayTrackData(it)
            viewModel.preparePlayer(it.previewUrl)
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (_binding == null) return

                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            binding.overlay.visibility = View.VISIBLE
                            binding.overlay.alpha = 0.5f
                            viewModel.loadPlaylists()
                        }
                        else -> {}
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (_binding == null) return

                    val alpha = when {
                        slideOffset >= 0 -> {
                            0.5f + (0.5f * slideOffset)
                        }
                        else -> {
                            0.5f + (0.5f * slideOffset)
                        }
                    }

                    if (alpha <= 0f) {
                        binding.overlay.visibility = View.GONE
                    } else {
                        binding.overlay.visibility = View.VISIBLE
                        binding.overlay.alpha = alpha
                    }
                }
            })
        }
    }

    private fun setupRecyclerview() {
        adapter = AddToPlaylistAdapter { playlist ->
            viewModel.onPlaylistSelected(playlist)
        }

        binding.bottomSheetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomSheetRecyclerView.adapter = adapter

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

        binding.LikeButton.setOnClickListener{
            viewModel.onFavoriteClicked()
        }

        binding.PlayButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.addButton.setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.bottomSheetButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_audioPlayerFragment_to_makePlaylistFragment2)
        }

        binding.overlay.setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updatePlayButton(state.playerState)
            binding.TrackTime.text = state.progressTime
            updateLikeButton(state.isFavorite)

            state.playlists?.let { playlists ->
                adapter.updatePlaylists(playlists)
            }

            state.toastMessageResId?.let { resId ->
                val message = if (state.toastArg != null) {
                    getString(resId, state.toastArg)
                } else{
                    getString(resId)
                }
                showCustomToast(message)
                viewModel.clearToast()
            }

            if (state.shouldCloseBottomSheet) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                viewModel.onBottomSheetClosed()
            }
        }
    }

    private fun showCustomToast(message: String) {
        val toastBinding = CustomToastBinding.inflate(layoutInflater, binding.root, false)
        toastBinding.toastText.text = message

        Toast(requireContext()).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 16)
            view = toastBinding.root
            show()
        }
    }

    private fun updateLikeButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.LikeButton.setImageResource(R.drawable.liked_button)
        } else {
            binding.LikeButton.setImageResource(R.drawable.like_button)
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