package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.presentation.App
import com.google.android.material.switchmaterial.SwitchMaterial

class Settings : AppCompatActivity() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        enableEdgeToEdge()

        settingsInteractor = Creator.provideSettingsInteractor(this)

        val buttonBack = findViewById<ImageView>(R.id.buttonback)
        val shareButton = findViewById<LinearLayout>(R.id.ShareButton)
        val supportButton = findViewById<LinearLayout>(R.id.SupportButton)
        val agreementButton = findViewById<LinearLayout>(R.id.AgreementButton)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)


        themeSwitcher.isChecked = settingsInteractor.getDarkTheme()

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }


        buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        shareButton.setOnClickListener{
            val textShare = getString(R.string.practicumURL)
            val shareIntent = Intent()

            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, textShare)
            shareIntent.type = "text/plain"

            startActivity(shareIntent)
        }

        agreementButton.setOnClickListener{
            val agreementUrl = getString(R.string.agreementURL)
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))

            startActivity(agreementIntent)
        }

        supportButton.setOnClickListener{
         val mail = getString(R.string.mail)
         val headMessage = getString(R.string.headMessage)
         val bodyMessage = getString(R.string.bodyMessage)

         val emailIntent = Intent(Intent.ACTION_SENDTO)
         emailIntent.data = Uri.parse("mailto:")
         emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
         emailIntent.putExtra(Intent.EXTRA_SUBJECT, headMessage)
         emailIntent.putExtra(Intent.EXTRA_TEXT, bodyMessage)

         startActivity(emailIntent)
        }

    }
}