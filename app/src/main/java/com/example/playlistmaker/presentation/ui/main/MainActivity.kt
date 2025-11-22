package com.example.playlistmaker.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.settings.Settings
import com.example.playlistmaker.presentation.ui.mediateka.Mediateka
import com.example.playlistmaker.presentation.ui.search.Search

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        val buttonSearch = findViewById<Button>(R.id.search)
        val buttonMediateka = findViewById<Button>(R.id.mediateka)
        val buttonSettings = findViewById<Button>(R.id.settings)

        val buttonClickListener: View.OnClickListener = object : View.OnClickListener{
            override fun onClick(v: View?){
                val displayIntent = Intent(v?.context, Search::class.java)
                v?.context?.startActivity(displayIntent)
            }
        }

        buttonMediateka.setOnClickListener{
            val displayIntent = Intent(this, Mediateka::class.java)

            startActivity(displayIntent)
        }

        buttonSettings.setOnClickListener{
            val displayIntent = Intent(this, Settings::class.java)

            startActivity(displayIntent)
        }

        buttonSearch.setOnClickListener(buttonClickListener)
    }


}