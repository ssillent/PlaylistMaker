package com.example.playlistmaker.ui.make_playlist.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.make_playlist.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class MakePlaylistViewModel(protected val interactor: PlaylistInteractor) : ViewModel() {

    protected val _state = MutableLiveData(MakePlaylistState())
    val state: LiveData<MakePlaylistState> = _state

    fun onNameChanged(name: String) {
        _state.value = _state.value?.copy(
            name = name,
            isNameValid = name.isNotBlank()
        )
    }

    fun onDescriptionChanged(description: String) {
        _state.value = _state.value?.copy(description = description)
    }

    fun onImageSaved(path: String?, uri: Uri) {
        _state.value = _state.value?.copy(
            path = path,
            uri = uri
        )
    }

    open fun onBackPressed() {
        val currentState = _state.value
        if (hasUnsavedData(currentState)) {
            _state.value = currentState?.copy(showExitDialog = true)
        } else {
            _state.value = currentState?.copy(shouldNavigateBack = true)
        }
    }

    fun onExitConfirmed() {
        _state.value = _state.value?.copy(
            showExitDialog = false,
            shouldNavigateBack = true
        )
    }

    fun onExitCancelled() {
        _state.value = _state.value?.copy(showExitDialog = false)
    }

    open fun onCreatePlaylist() {
        val currentState = _state.value ?: return
        if (!currentState.isNameValid) return

        viewModelScope.launch {
            _state.value = currentState.copy(isSaving = true)

            val playlist = Playlist(
                playlistName = currentState.name,
                playlistDescription = currentState.description.takeIf { it.isNotBlank() },
                path = currentState.path,
                tracksID = emptyList(),
                tracksCount = 0
            )

            interactor.createPlaylist(playlist)

            _state.value = currentState.copy(
                isSaving = false,
                toastMessageResId = R.string.playlist_created,
                toastArg = currentState.name,
                shouldNavigateBack = true
            )

            delay(1000)
            clearToast()
        }

    }

     fun clearToast() {
        _state.value = _state.value?.copy(
            toastMessageResId = null,
            toastArg = null
        )
    }

    fun onNavigationComplete() {
        _state.value = _state.value?.copy(shouldNavigateBack = false)
    }

    private fun hasUnsavedData(state: MakePlaylistState?): Boolean {
        return state?.let {
            it.name.isNotBlank() || it.description.isNotBlank() || it.uri != null || it.path != null
        } ?: false
    }

}

data class MakePlaylistState(
    val name: String = "",
    val description: String = "",
    val uri: Uri? = null,
    val path: String? = null,
    val isNameValid: Boolean = false,
    val isSaving: Boolean = false,
    val showExitDialog: Boolean = false,
    val shouldNavigateBack: Boolean = false,
    val toastMessageResId: Int? = null,
    val toastArg: String? = null
)