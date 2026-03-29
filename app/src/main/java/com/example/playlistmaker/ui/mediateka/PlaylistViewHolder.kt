package com.example.playlistmaker.ui.mediateka

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ViewPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.utils.formatTracks
import java.io.File

class PlaylistViewHolder (private val binding: ViewPlaylistBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(playlist: Playlist) {
        binding.titleText.text = playlist.playlistName

        binding.trackCount.text = playlist.tracksCount.formatTracks()

        val cornerRaius = dpToPx(8f, itemView.context)

        if (!playlist.path.isNullOrEmpty()) {
            val file = File(playlist.path)
            if (file.exists()) {
                Glide.with((itemView.context))
                    .load(file)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(cornerRaius))
                    .into(binding.imageView)
            }
        } else {
            Glide.with((itemView.context))
                .load(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornerRaius))
                .into(binding.imageView)
        }

    }

}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}