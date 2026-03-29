package com.example.playlistmaker.ui.edit_playlist.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.make_playlist.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.ui.make_playlist.view_model.MakePlaylistState
import com.example.playlistmaker.ui.make_playlist.view_model.MakePlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewModel(interactor: PlaylistInteractor, private val playlistToEdit: Playlist): MakePlaylistViewModel(interactor) {

    init {
        _state.value = MakePlaylistState(
            name = playlistToEdit.playlistName,
            description = playlistToEdit.playlistDescription ?: "",
            path = playlistToEdit.path,
            isNameValid = true
        )
    }

    override fun onCreatePlaylist() {
        val currentState = _state.value ?: return
        if (!currentState.isNameValid) return

        viewModelScope.launch {
            _state.value = currentState.copy(isSaving = true)

            val updatedPlaylist = Playlist(
                playlistId = playlistToEdit.playlistId,
                playlistName = currentState.name,
                playlistDescription = currentState.description.takeIf { it.isNotBlank() },
                path = currentState.path,
                tracksID = playlistToEdit.tracksID,
                tracksCount = playlistToEdit.tracksCount
            )

            interactor.updatePlaylist(updatedPlaylist)

            _state.value = currentState.copy(
                isSaving = false,
                toastMessageResId = R.string.playlist_edited,
                toastArg = currentState.name,
                shouldNavigateBack = true
            )
        }
    }

    override fun onBackPressed() {
        _state.value = _state.value?.copy(shouldNavigateBack = true)
    }

}