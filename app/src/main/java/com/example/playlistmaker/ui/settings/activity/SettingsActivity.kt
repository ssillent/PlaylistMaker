package com.example.playlistmaker.ui.settings.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextSwitcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.settings.interactor.SettingsInteractor
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class Settings : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial

    private val viewModel: SettingsViewModel by viewModels {
       SettingsViewModel.getFactory()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        enableEdgeToEdge()

        initViews()
        setupListeners()
        setupObservers()
    }

    private fun initViews() {
        themeSwitcher = findViewById(R.id.themeSwitcher)

        findViewById<ImageView>(R.id.buttonback).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSettings(checked)
        }

        findViewById<LinearLayout>(R.id.ShareButton).setOnClickListener {
            viewModel.shareLink()
        }

        findViewById<LinearLayout>(R.id.SupportButton).setOnClickListener {
            viewModel.openSupport()
        }

        findViewById<LinearLayout>(R.id.AgreementButton).setOnClickListener {
            viewModel.openTerms()
        }
    }

    private fun setupObservers(){
        viewModel.themeSettings.observe(this) { settings ->
            themeSwitcher.setOnCheckedChangeListener(null)
            themeSwitcher.isChecked = settings.darkthemeEnabled
            themeSwitcher.setOnCheckedChangeListener { _, checked ->
                viewModel.updateThemeSettings(checked)
            }
        }
    }

}