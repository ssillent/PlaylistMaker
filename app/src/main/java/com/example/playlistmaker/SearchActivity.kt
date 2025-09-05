package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class Search : AppCompatActivity() {

    var searchText: String = DEFAULT_TEXT
    lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchEditText = findViewById<EditText>(R.id.SearchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val searchButtonBack = findViewById<ImageView>(R.id.SearchBackButton)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val tracks = arrayListOf(
            Track(
                trackName = getString(R.string.track1name),
                authorName = getString(R.string.track1singer),
                trackTime = getString(R.string.track1time),
                artworkUrl100 = getString(R.string.track1url)
            ),
            Track(
                trackName = getString(R.string.track2name),
                authorName = getString(R.string.track2singer),
                trackTime = getString(R.string.track2time),
                artworkUrl100 = getString(R.string.track2url)
            ),
            Track(
                trackName = getString(R.string.track3name),
                authorName = getString(R.string.track3singer),
                trackTime = getString(R.string.track3time),
                artworkUrl100 = getString(R.string.track3url)
            ),
            Track(
                trackName = getString(R.string.track4name),
                authorName = getString(R.string.track4singer),
                trackTime = getString(R.string.track4time),
                artworkUrl100 = getString(R.string.track4url)
            ),
            Track(
                trackName = getString(R.string.track5name),
                authorName = getString(R.string.track5singer),
                trackTime = getString(R.string.track5time),
                artworkUrl100 = getString(R.string.track5url)
            )
        )

        val searchAdapter = SearchAdapter(tracks)
        recyclerView.adapter = searchAdapter


        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            searchEditText.setText(searchText)
        }

        searchButtonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        searchEditText.requestFocus()

        clearButton.setOnClickListener {
            searchEditText.setText("")

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
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
        private const val DEFAULT_TEXT = ""
    }


}