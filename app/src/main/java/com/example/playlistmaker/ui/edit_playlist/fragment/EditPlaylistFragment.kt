package com.example.playlistmaker.ui.edit_playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MakePlaylistFragmentBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.ui.edit_playlist.viewmodel.EditPlaylistViewModel
import com.example.playlistmaker.ui.make_playlist.fragments.MakePlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment: MakePlaylistFragment() {

    private val playlist: Playlist by lazy {
        arguments?.getSerializable(PLAYLIST_ARG) as? Playlist ?: throw IllegalArgumentException()
    }

    companion object {
        private const val PLAYLIST_ARG = "playlist"
    }

    override val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(playlist)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MakePlaylistFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEditMode()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupEditMode() {
        binding.createPlaylistButton.text = getString(R.string.save)
        binding.createPlaylistText.text = getString(R.string.edit2)

        binding.playlistNameEditText.setText(playlist.playlistName)
        binding.playlistDescriptionEditText.setText(playlist.playlistDescription ?: "")

        playlist.path?.let { path ->
            val file = java.io.File(path)
            if (file.exists()) {
                val pxSize = dpToPx(8f, requireContext())
                Glide.with(this)
                    .load(file)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(pxSize))
                    .into(binding.choosePictureImage)
                binding.choosePictureImage.setPadding(0, 0, 0, 0)
            }
        }
    }
}