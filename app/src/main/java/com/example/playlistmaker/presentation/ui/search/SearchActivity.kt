package com.example.playlistmaker.presentation.ui.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.presentation.ui.audioplayer.AudioPlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.network.SongApiService
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Search : AppCompatActivity() {

    var searchText: String = DEFAULT_TEXT
    lateinit var searchEditText: EditText


    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private lateinit var trackInteractor: TrackInteractor
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNoInternet: LinearLayout
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var historyAdapter: SearchAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private val historyTracks = mutableListOf<Track>()

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val seacrhRunnable = Runnable { searchTrack() }



    private val tracks = mutableListOf<Track>()

    private var lastSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()

        trackInteractor = Creator.provideTrackInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)


        searchEditText = findViewById(R.id.SearchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val searchButtonBack = findViewById<ImageView>(R.id.SearchBackButton)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderNothingFound = findViewById(R.id.NothingFound)
        placeholderNoInternet = findViewById(R.id.NoInternet)
        val updateButton = findViewById<Button>(R.id.updateButton)
        searchHistoryLayout = findViewById(R.id.SearchHistory)
        searchHistoryRecyclerView = findViewById(R.id.searchHistoryRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressbar)

        searchAdapter = SearchAdapter(tracks) { track -> onTrackClicked(track)}
        recyclerView.adapter = searchAdapter

        historyAdapter = SearchAdapter(historyTracks) { track -> onTrackClicked(track)}
        searchHistoryRecyclerView.adapter = historyAdapter

        searchEditText.requestFocus()

        loadSearchHistory()

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            val savedTracks = savedInstanceState.getSerializable(TRACKS_KEY) as? ArrayList<Track>
            if (savedTracks != null) {
                tracks.clear()
                tracks.addAll(savedTracks)
            }
            searchEditText.setText(searchText)
        }



        fun retryLastSearch() {
                searchEditText.setText(lastSearchQuery)
                searchTrack()
        }



        searchButtonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }



        updateButton.setOnClickListener{
            retryLastSearch()
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()

        }

        clearHistoryButton.setOnClickListener{
            searchHistoryInteractor.clearHistory()
            historyTracks.clear()
            historyAdapter.notifyDataSetChanged()
            showSearchHistory()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true

            } else {
                false
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)

                searchText = s?.toString() ?: ""

                if (s.isNullOrEmpty()) {
                    tracks.clear()
                    searchAdapter.notifyDataSetChanged()
                    hidePlaceholderNoInternet()
                    hidePlaceholderNothingFound()
                    hideSearchResult()
                }

                if (searchEditText.hasFocus() && s.isNullOrEmpty()) {
                    showSearchHistory()
                } else {
                    hideSearchHistory()
                }

                searchDebounce()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)

        searchEditText.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus && searchEditText.text.isNullOrEmpty()){
                showSearchHistory()
            } else{
                hideSearchHistory()
            }
        }


    }

    private fun searchTrack(){
        val query = searchEditText.text.toString().trim()

        if (query.isNotEmpty()){

            lastSearchQuery = query

            hidePlaceholderNoInternet()
            hidePlaceholderNothingFound()
            hideSearchHistory()
            hideSearchResult()
            showProgressBar()

            trackInteractor.searchTrack(query, object : TrackInteractor.TrackConsumer {

                override fun consume(foundTracks: List<Track>) {
                handler.post{
                    hideProgressBar()

                if(foundTracks.isNotEmpty()) {
                    tracks.clear()
                    tracks.addAll(foundTracks)
                    searchAdapter.notifyDataSetChanged()
                    showSearchResult()
                } else {
                    showPlaceholderNothingFound()
                    hideSearchResult()
                    tracks.clear()
                    searchAdapter.notifyDataSetChanged()
                }
            }
            }
        })
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(seacrhRunnable)
        handler.postDelayed(seacrhRunnable, SEARCH_DELAY)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DELAY)
        }
        return current
    }

    private fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun onTrackClicked(track: Track){
        if (clickDebounce()) {
            searchHistoryInteractor.addToHistory(track)
            loadSearchHistory()
            hideKeyboard()

            startActivity(AudioPlayerActivity.getIntent(this, track))
        }
    }

    private fun loadSearchHistory(){
        historyTracks.clear()
        historyTracks.addAll(searchHistoryInteractor.getHistory())
        historyAdapter.notifyDataSetChanged()
        showSearchHistory()
    }


    private fun showSearchHistory(){
        if (historyTracks.isNotEmpty() && searchEditText.text.isNullOrEmpty() && tracks.isEmpty()) {
            searchHistoryLayout.visibility = View.VISIBLE
            hideSearchResult()
            hidePlaceholderNothingFound()
            hidePlaceholderNoInternet()
        } else {
            searchHistoryLayout.visibility = View.GONE
        }
    }

    private fun hideSearchHistory(){
        searchHistoryLayout.visibility = View.GONE
    }

    private fun showPlaceholderNothingFound() {
        placeholderNothingFound.visibility = View.VISIBLE
    }

    private fun hidePlaceholderNothingFound() {
        placeholderNothingFound.visibility = View.GONE
    }
    private fun showPlaceholderNoInternet() {
        placeholderNoInternet.visibility = View.VISIBLE
    }

    private fun hidePlaceholderNoInternet() {
        placeholderNoInternet.visibility = View.GONE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
    }

    private fun hideSearchResult(){
        recyclerView.visibility = View.GONE
    }

    private fun showSearchResult(){
        recyclerView.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
        outState.putSerializable(TRACKS_KEY, ArrayList(tracks))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, DEFAULT_TEXT)
        searchEditText.setText(searchText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }


    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
        private const val TRACKS_KEY = "tracks_key"
        private const val DEFAULT_TEXT = ""
        private const val CLICK_DELAY = 1000L
        private const val SEARCH_DELAY = 2000L
    }


}