package com.example.playlistmaker.ui.mediateka.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FavoriteTracksFragmentBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.mediateka.view_model.FavoritesState
import com.example.playlistmaker.ui.mediateka.view_model.FavoritesViewModel
import com.example.playlistmaker.ui.search.SearchAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FavoriteTracksFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoriteTracksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupObservers()
    }

    private fun setupAdapters() {
        adapter = SearchAdapter { track ->
            val bundle = Bundle().apply {
                putSerializable("track", track)
            }
            findNavController().navigate(R.id.action_mediatekaFragment_to_audioPlayerFragment, bundle)
        }
        binding.favoriteTracksRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Empty -> showEmpty()
                is FavoritesState.Content -> showContent(state.tracks)
            }
        }
    }

    private fun showEmpty() {
        binding.favoriteTracksRecyclerView.isVisible = false
        binding.emptyFavoriteTracks.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        binding.favoriteTracksRecyclerView.isVisible = true
        binding.emptyFavoriteTracks.isVisible = false
        adapter.updateTracks(tracks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }

}