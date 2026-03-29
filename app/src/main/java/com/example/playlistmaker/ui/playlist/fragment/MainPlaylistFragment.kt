package com.example.playlistmaker.ui.playlist.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.CustomToastBinding
import com.example.playlistmaker.databinding.FragmentMainPlaylistBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.playlist.view_model.MainPlaylistViewModel
import com.example.playlistmaker.ui.playlist.view_model.PlaylistUiState
import com.example.playlistmaker.ui.search.PlaylistTrackAdapter
import com.example.playlistmaker.utils.dpToPx
import com.example.playlistmaker.utils.formatMinutes
import com.example.playlistmaker.utils.formatTracks
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class MainPlaylistFragment: Fragment() {

    private var _binding: FragmentMainPlaylistBinding? = null
    private val binding get() = _binding!!

    private val playlistID by lazy {
        arguments?.getInt(PLAYLIST_ID_ARG) ?: 0
    }

    private val viewModel: MainPlaylistViewModel by viewModel {
        parametersOf(playlistID)
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var optionsBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var adapter: PlaylistTrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheet()
        setupOptionsBottomSheet()
        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylistData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBottomSheet() {
            bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
                isHideable = false
                isDraggable = true

                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_EXPANDED -> {
                                binding.overlay.visibility = View.VISIBLE
                                binding.overlay.alpha = 0.5f
                            }
                            BottomSheetBehavior.STATE_COLLAPSED -> {
                                binding.overlay.visibility = View.GONE
                            }
                            else -> {}
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        if (slideOffset > 0) {
                            binding.overlay.visibility = View.VISIBLE
                            binding.overlay.alpha = slideOffset * 0.5f
                        } else {
                            binding.overlay.visibility = View.GONE
                        }
                    }
                })
            }

    }

    private fun updateBottomSheetHeight() {
        val currentBinding = _binding ?: return

        val contentHeight = currentBinding.mainContent.height
        val screenHeight = resources.displayMetrics.heightPixels
        val marginTop = 24.dpToPx(requireContext())

        val availableHeight = screenHeight - contentHeight - marginTop
        val minHeight = 200.dpToPx(requireContext())
        val finalHeight = maxOf(availableHeight, minHeight)

        val params = currentBinding.bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
        params.topMargin = marginTop
        currentBinding.bottomSheet.layoutParams = params

        bottomSheetBehavior.peekHeight = finalHeight
    }

    private fun setupOptionsBottomSheet() {
        optionsBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetOptions).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            isDraggable = true
            peekHeight = 0


            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    _binding?.let {

                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                            bottomSheetBehavior.isDraggable = true
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            binding.overlay.visibility = View.VISIBLE
                            binding.overlay.alpha = 0.5f
                            bottomSheetBehavior.isDraggable = false
                        }
                        else -> {}
                    }
                  }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    _binding?.let {
                    when {
                        slideOffset < 0 -> {
                            binding.overlay.visibility = View.GONE
                            bottomSheetBehavior.isDraggable = true
                        }
                        slideOffset > 0 -> {
                            binding.overlay.visibility = View.VISIBLE
                            binding.overlay.alpha = 0.5f * slideOffset
                            bottomSheetBehavior.isDraggable = false
                        }
                        else -> {
                            binding.overlay.visibility = View.GONE
                            bottomSheetBehavior.isDraggable = true
                        }
                    }
                  }
                }
            })
        }

        binding.bottomSheetOptions.post {
            val params = binding.bottomSheetOptions.layoutParams
            params.height = 350.dpToPx(requireContext())
            binding.bottomSheetOptions.layoutParams = params
        }

        binding.buttonShare.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.sharePlaylist()
        }

        binding.buttonEdit.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            viewModel.state.value?.playlist?.let { playlist ->
                val bundle = Bundle().apply {
                    putSerializable("playlist", playlist)
                }

                findNavController().navigate(R.id.action_mainPlaylistFragment_to_EditPlaylistFragment, bundle)
            }
        }

        binding.buttonDelete.setOnClickListener {
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

        binding.overlay.setOnClickListener {
            if (optionsBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PlaylistTrackAdapter(
            onTrackClick = { track ->
                val bundle = Bundle().apply {
                    putSerializable("track", track)
                }
                findNavController().navigate(R.id.action_mainPlaylistFragment_to_audioPlayerFragment, bundle)
            },
            onTrackLongClick = { track ->
                showDeleteDialog(track)
            }
        )

        binding.bottomSheetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomSheetRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.share.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.moreOptions.setOnClickListener {
            displayBottomSheetPlaylistInfo()
            optionsBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            displayPlaylistInfo(state)

            adapter.updateTracks(state.tracks)

            if (state.tracks.isEmpty()) {
                binding.bottomSheetRecyclerView.visibility = View.GONE
                binding.emptyPlaylistPlaceholder.visibility = View.VISIBLE
            } else {
                binding.bottomSheetRecyclerView.visibility = View.VISIBLE
                binding.emptyPlaylistPlaceholder.visibility = View.GONE
            }

            updateBottomSheetHeight()

            if (state.showEmptyShareMessage) {
                showEmptyShareMessage()
                viewModel.resetEmptyShareMessage()
            }

            if (state.navigateBack) {
                findNavController().popBackStack()
            }
        }
    }

    private fun displayBottomSheetPlaylistInfo() {
        val state = viewModel.state.value ?: return
        val playlist = state.playlist ?: return

        binding.playlistName.text = playlist.playlistName
        binding.trackCount2.text = state.tracksCount.formatTracks()

        if (!playlist.path.isNullOrEmpty()) {
            Glide.with(this)
                .load(playlist.path)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.playlistIcon)
        } else {
            binding.playlistIcon.setImageResource(R.drawable.placeholder)
        }
    }

    private fun displayPlaylistInfo(state: PlaylistUiState) {
        val playlist = state.playlist ?: return

        binding.titleText.text = playlist.playlistName

        if (!playlist.playlistDescription.isNullOrEmpty()) {
            binding.description.text = playlist.playlistDescription
            binding.description.visibility = View.VISIBLE
        } else {
            binding.description.visibility = View.GONE
        }

        binding.timeCount.text = state.totalDurationMinutes.formatMinutes()
        binding.trackCount.text = state.tracksCount.formatTracks()

        loadPlaylistImage(playlist.path)

        binding.description.post{ updateBottomSheetHeight() }
    }


    private fun loadPlaylistImage(path: String?) {
        binding.playlistImage.setPadding(0,0,0,0)

        if (!path.isNullOrEmpty()) {
            Glide.with(this)
                .load(path)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.playlistImage)
        } else {
            binding.playlistImage.setImageResource(R.drawable.placeholder)
        }
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_title))
            .setMessage(getString(R.string.delete_playlist_message))
            .setNegativeButton(getString(R.string.delete_playlist_no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete_playlist_yes)) { dialog, _ ->
                viewModel.deletePlaylist()
                dialog.dismiss()
            }
            .show()
    }

    private fun showEmptyShareMessage() {
        val toastBinding = CustomToastBinding.inflate(layoutInflater, binding.root, false)
        toastBinding.toastText.text = getString(R.string.empty_playlist_message)

        Toast(requireContext()).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 16)
            view = toastBinding.root
            show()
        }
    }

    private fun showDeleteDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track_title))
            .setMessage(getString(R.string.delete_track_message, track.trackName))
            .setNegativeButton(getString(R.string.delete_track_no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete_track_yes)) { dialog, _ ->
                viewModel.deleteTrack(track)
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        private const val PLAYLIST_ID_ARG = "playlist_id"
    }
}