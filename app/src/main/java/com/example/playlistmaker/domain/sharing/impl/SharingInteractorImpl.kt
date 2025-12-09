package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.ui.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.model.StringLinks

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator, private val stringLinks: StringLinks): SharingInteractor {

    override fun shareLink() {
        externalNavigator.shareLink(getShareLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    override fun getShareLink(): String {
        return stringLinks.shareLink
    }

    override fun getTermsLink(): String {
        return stringLinks.termsLink
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = stringLinks.supportEmail,
            subject = stringLinks.supportSubject,
            body = stringLinks.supportBody
        )
    }
}