package com.example.playlistmaker.domain.sharing.interactor

import com.example.playlistmaker.domain.sharing.model.EmailData

interface SharingInteractor {

    fun shareLink()
    fun openTerms()
    fun openSupport()

    fun getShareLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): EmailData
}