package com.example.playlistmaker.ui.audioplayer

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ViewAddtoplaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import java.io.File

class AddToPlaylistViewHolder(private val binding: ViewAddtoplaylistBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.playlistName

        binding.trackCount.text = itemView.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )

        val cornerRaius = dpToPx(4f, itemView.context)

        if (!playlist.path.isNullOrEmpty()) {
            val file = File(playlist.path)
            if (file.exists()) {
                Glide.with((itemView.context))
                    .load(file)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(cornerRaius))
                    .into(binding.playlistIcon)
            }
        } else {
            Glide.with((itemView.context))
                .load(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornerRaius))
                .into(binding.playlistIcon)
        }

    }

}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}
