package com.example.playlistmaker.ui.mediateka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistsFragmentBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.ui.mediateka.PlaylistAdapter
import com.example.playlistmaker.ui.mediateka.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: PlaylistsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var adapter: PlaylistAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlaylistsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    private fun setupUI() {
        binding.playlistFragmentRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistFragmentRecyclerView.setHasFixedSize(true)

        adapter = PlaylistAdapter { playlist ->
            val bundle = Bundle().apply {
                putInt("playlist_id", playlist.playlistId)
            }
            findNavController().navigate(R.id.action_mediatekaFragment_to_mainPlaylistFragment, bundle)
        }

        binding.playlistFragmentRecyclerView.adapter = adapter

        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediatekaFragment_to_makePlaylistFragment2)
        }
    }

    private fun setupObservers() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            updateUI(playlists)
        }
    }

    private fun updateUI(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            binding.playlistFragmentRecyclerView.visibility = View.GONE
            binding.placeholderLayout.visibility = View.VISIBLE
        } else {
            binding.playlistFragmentRecyclerView.visibility = View.VISIBLE
            binding.placeholderLayout.visibility = View.GONE
            adapter.updatePlaylists(playlists)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

}