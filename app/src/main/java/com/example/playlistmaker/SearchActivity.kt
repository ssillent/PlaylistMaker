package com.example.playlistmaker

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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Search : AppCompatActivity() {

    var searchText: String = DEFAULT_TEXT
    lateinit var searchEditText: EditText

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val ituneService = retrofit.create(SongApiService::class.java)

    private val tracks = mutableListOf<Track>()

    private var lastSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchEditText = findViewById(R.id.SearchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val searchButtonBack = findViewById<ImageView>(R.id.SearchBackButton)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val placeholderNothingFound = findViewById<LinearLayout>(R.id.NothingFound)
        val placeholderNoInternet = findViewById<LinearLayout>(R.id.NoInternet)
        val updateButton = findViewById<Button>(R.id.updateButton)

        val searchAdapter = SearchAdapter(tracks)
        recyclerView.adapter = searchAdapter

        searchEditText.requestFocus()

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            val savedTracks = savedInstanceState.getSerializable(TRACKS_KEY) as? ArrayList<Track>
            if (savedTracks != null) {
                tracks.clear()
                tracks.addAll(savedTracks)
            }
            searchEditText.setText(searchText)
        }

        fun showPlaceholderNothingFound() {
            placeholderNothingFound.visibility = View.VISIBLE
        }

        fun hidePlaceholderNothingFound() {
            placeholderNothingFound.visibility = View.GONE
        }
        fun showPlaceholderNoInternet() {
            placeholderNoInternet.visibility = View.VISIBLE
        }

        fun hidePlaceholderNoInternet() {
            placeholderNoInternet.visibility = View.GONE
        }

        fun searchTrack(){
            val query = searchEditText.text.toString().trim()

            if (query.isNotEmpty()){

                lastSearchQuery = query

                hidePlaceholderNoInternet()
                hidePlaceholderNothingFound()

                ituneService.searchTrack(query).enqueue(object : Callback<TrackResponse>{

                    override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {

                        if (response.isSuccessful && response.code() == 200) {
                            val trackResponse = response.body()

                            if(trackResponse != null && trackResponse.results.isNotEmpty()){
                                tracks.clear()
                                tracks.addAll(trackResponse.results)
                                searchAdapter.notifyDataSetChanged()

                            }
                            else {
                                showPlaceholderNothingFound()
                                hidePlaceholderNoInternet()
                                tracks.clear()
                                searchAdapter.notifyDataSetChanged()
                            }
                        } else {
                            showPlaceholderNoInternet()
                            hidePlaceholderNothingFound()
                            tracks.clear()
                            searchAdapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showPlaceholderNoInternet()
                        hidePlaceholderNothingFound()
                        tracks.clear()
                        searchAdapter.notifyDataSetChanged()
                    }
                })

            }
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

            tracks.clear()
            searchAdapter.notifyDataSetChanged()

            hidePlaceholderNoInternet()
            hidePlaceholderNothingFound()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
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
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)


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
    }


}