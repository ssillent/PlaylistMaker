package com.example.playlistmaker.domain.sharing.model

import javax.security.auth.Subject

data class EmailData(
    val email: String,
    val subject: String,
    val body: String
)

data class StringLinks(
    val shareLink: String,
    val termsLink: String,
    val supportEmail: String,
    val supportSubject: String,
    val supportBody: String
)
