package com.example.playlistmaker.ui.search.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.SearchAdapter
import com.example.playlistmaker.ui.search.view_model.SearchState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var historyAdapter: SearchAdapter

    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerViews()
        setupObservers()
        setupListeners()

        binding.SearchEditText.requestFocus()
    }

    override fun onResume() {
        super.onResume()

        if (binding.SearchEditText.text.isNullOrEmpty()) {
            viewModel.loadSearchHistory()
        } else {
            viewModel.searchWithoutDebounce(binding.SearchEditText.text.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerViews() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = SearchAdapter { track -> onTrackClicked(track) }
        binding.recyclerView.adapter = searchAdapter

        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = SearchAdapter { track -> onTrackClicked(track) }
        binding.searchHistoryRecyclerView.adapter = historyAdapter
    }

    private fun setupObservers() {
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            hideAllViews()

            when (state) {
                is SearchState.Default -> {

                }

                is SearchState.Loading,
                is SearchState.LoadingHistory -> {
                    binding.progressbar.isVisible = true
                }

                is SearchState.History -> {
                    if (state.tracks.isNotEmpty()) {
                        historyAdapter.updateTracks(state.tracks)
                        binding.SearchHistory.isVisible = true
                    }
                }

                is SearchState.Content -> {
                    searchAdapter.updateTracks(state.tracks)
                    binding.recyclerView.isVisible = true
                }

                is SearchState.Empty -> {
                    binding.NothingFound.isVisible = true
                }

                is SearchState.Error -> {
                    binding.NoInternet.isVisible = true
                }
            }
        }
    }

    private fun setupListeners() {

        binding.clearButton.setOnClickListener {
            binding.SearchEditText.setText("")
            hideKeyboard()
            viewModel.loadSearchHistory()
        }

        binding.SearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                viewModel.searchDebounce(s.toString())

                if (s.isNullOrEmpty()) {
                    viewModel.loadSearchHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.SearchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                viewModel.searchWithoutDebounce(binding.SearchEditText.text.toString())
                true
            } else {
                false
            }
        }

        binding.SearchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.SearchEditText.text.isNullOrEmpty()) {
                viewModel.loadSearchHistory()
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.updateButton.setOnClickListener {
            val query = binding.SearchEditText.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchWithoutDebounce(query)
            }
        }
    }

    private fun hideAllViews() {
        binding.recyclerView.isVisible = false
        binding.progressbar.isVisible = false
        binding.NothingFound.isVisible = false
        binding.NoInternet.isVisible = false
        binding.SearchHistory.isVisible = false
    }

    private fun onTrackClicked(track: Track) {
        hideKeyboard()

        val bundle = Bundle().apply {
            putSerializable("track", track)
        }

        viewModel.onTrackClickDebounce(track) {
            findNavController().navigate(
                R.id.action_searchFragment_to_audioPlayerFragment,
                bundle
            )
        }
    }



    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.SearchEditText.windowToken, 0)
    }


}