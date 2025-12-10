package com.example.playlistmaker.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.settings.activity.Settings
import com.example.playlistmaker.ui.mediateka.activity.Mediateka
import com.example.playlistmaker.ui.search.activity.Search

class MainActivity : AppCompatActivity() {

    private lateinit var searchButton: Button
    private lateinit var mediatekaButton: Button
    private lateinit var settingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()


        initViews()
        setupListeners()
    }

    private fun initViews() {
        searchButton = findViewById(R.id.search)
        mediatekaButton = findViewById(R.id.mediateka)
        settingsButton = findViewById(R.id.settings)
    }

    private fun setupListeners() {
        searchButton.setOnClickListener {
            navigateToSearch()
        }

        mediatekaButton.setOnClickListener {
            navigateToMediateka()
        }

        settingsButton.setOnClickListener {
            navigateToSettings()
        }
    }

    private fun navigateToSearch() {
        val intent = Intent(this, Search::class.java)
        startActivity(intent)
    }
    private fun navigateToMediateka() {
        val intent = Intent(this, Mediateka::class.java)
        startActivity(intent)
    }

    private fun navigateToSettings() {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }


}