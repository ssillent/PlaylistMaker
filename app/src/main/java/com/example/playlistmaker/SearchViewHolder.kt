package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private val songName: TextView = itemView.findViewById(R.id.track_name)
    private val authorName: TextView = itemView.findViewById(R.id.track_author)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val trackIcon: ImageView = itemView.findViewById(R.id.track_icon)

    fun bind(track: Track){
        songName.text = track.trackName
        authorName.text = track.authorName
        trackTime.text = track.trackTime
        val pxsize = dpToPx(2f, itemView.context)

        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(pxsize))
            .placeholder(R.drawable.placeholder)
            .into(trackIcon)
    }

}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}