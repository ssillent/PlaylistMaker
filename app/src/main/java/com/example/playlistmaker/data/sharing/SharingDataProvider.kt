package com.example.playlistmaker.data.sharing

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.model.StringLinks

class SharingDataProvider(private val context: Context) {


    fun getSharingConfig(): StringLinks {
        return StringLinks(
            shareLink = context.getString(R.string.practicumURL),
            termsLink = context.getString(R.string.agreementURL),
            supportEmail = context.getString(R.string.mail),
            supportSubject = context.getString(R.string.headMessage),
            supportBody = context.getString(R.string.bodyMessage)
        )
    }

}