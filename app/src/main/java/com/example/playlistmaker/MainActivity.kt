package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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