package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.ui.audioplayer.activity.AudioPlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.SearchAdapter
import com.example.playlistmaker.ui.search.view_model.SearchState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class Search : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNoInternet: LinearLayout
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var updateButton: Button

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var historyAdapter: SearchAdapter

    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()

        initViews()

        recyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = SearchAdapter { track -> onTrackClicked(track) }
        recyclerView.adapter = searchAdapter

        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = SearchAdapter { track -> onTrackClicked(track) }
        searchHistoryRecyclerView.adapter = historyAdapter

        setupObservers()
        setupListeners()


        searchEditText.requestFocus()
        showKeyboard()

    }

    private fun initViews() {
        searchEditText = findViewById(R.id.SearchEditText)
        clearButton = findViewById(R.id.clearButton)
        backButton = findViewById(R.id.SearchBackButton)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressbar)
        searchHistoryRecyclerView = findViewById(R.id.searchHistoryRecyclerView)
        placeholderNothingFound = findViewById(R.id.NothingFound)
        placeholderNoInternet = findViewById(R.id.NoInternet)
        searchHistoryLayout = findViewById(R.id.SearchHistory)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        updateButton = findViewById(R.id.updateButton)
    }

    private fun setupObservers() {
        viewModel.searchState.observe(this) { state ->
            hideAllViews()

            when (state) {
                is SearchState.Default -> {

                }

                is SearchState.Loading,
                is SearchState.LoadingHistory -> {
                    progressBar.isVisible = true
                }

                is SearchState.History -> {
                    if (state.tracks.isNotEmpty()) {
                        historyAdapter.updateTracks(state.tracks)
                        searchHistoryLayout.isVisible = true
                    }
                }

                is SearchState.Content -> {
                    searchAdapter.updateTracks(state.tracks)
                    recyclerView.isVisible = true
                }

                is SearchState.Empty -> {
                    placeholderNothingFound.isVisible = true
                }

                is SearchState.Error -> {
                    placeholderNoInternet.isVisible = true
                }
            }
        }
    }

    private fun setupListeners() {

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            viewModel.loadSearchHistory()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                viewModel.searchDebounce(s.toString())

                if (s.isNullOrEmpty()) {
                    viewModel.loadSearchHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                viewModel.searchWithoutDebounce(searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isNullOrEmpty()) {
                viewModel.loadSearchHistory()
            }
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        updateButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchWithoutDebounce(query)
            }
        }

    }


    private fun hideAllViews() {
        recyclerView.isVisible = false
        progressBar.isVisible = false
        placeholderNothingFound.isVisible = false
        placeholderNoInternet.isVisible = false
        searchHistoryLayout.isVisible = false
    }

    override fun onResume() {
        super.onResume()

        if (searchEditText.text.isNullOrEmpty()) {

            viewModel.loadSearchHistory()
        } else {

            viewModel.searchWithoutDebounce(searchEditText.text.toString())
        }
    }

    private fun onTrackClicked(track: Track) {
        hideKeyboard()

        viewModel.addToHistory(track)

        val intent = AudioPlayerActivity.getIntent(this, track)
        startActivity(intent)
    }

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}