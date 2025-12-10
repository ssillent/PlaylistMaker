package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track


class SearchAdapter(
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>(){

    private var tracks: List<Track> = emptyList()

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_track, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackClick(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}