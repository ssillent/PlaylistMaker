package com.example.playlistmaker.ui.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.domain.sharing.model.EmailData

class ExternalNavigator(private val context: Context) {

    fun shareLink(link: String){
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity((intent))
    }

    fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openEmail(emailData: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

}