package com.example.playlistmaker.ui.audioplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ViewAddtoplaylistBinding
import com.example.playlistmaker.databinding.ViewPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.ui.mediateka.PlaylistViewHolder

class AddToPlaylistAdapter(private val onPlaylistClick: (Playlist) -> Unit): RecyclerView.Adapter<AddToPlaylistViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddToPlaylistViewHolder {
        val binding = ViewAddtoplaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddToPlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddToPlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onPlaylistClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size
}