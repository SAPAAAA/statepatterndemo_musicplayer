package com.example.musicplayer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.state.PlayerContext
import com.example.musicplayer.domain.model.SongRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerContext: PlayerContext,
    private val songRepository: SongRepository
) : ViewModel() {
    val uiState = combine(
        playerContext.currentState,
        playerContext.currentSong,
        playerContext.isPlaying,
        playerContext.currentTimestampMs,
        playerContext.currentSongList,
    ) { state, song, playing, timestamp, list ->
        PlayerUiState(
            currentState = state.getType(),
            currentSong = song,
            isPlaying = playing,
            currentTimestampMs = timestamp,
            songList = list
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3000),
        initialValue = PlayerUiState()
    )

    init {
        viewModelScope.launch {
            loadLibrary()
        }
    }

    private fun loadLibrary() {
        viewModelScope.launch {
            playerContext.addSongs(songRepository.getAllSongs())
        }
    }

    fun onNewSongSelected(uriString: String) {
        viewModelScope.launch {
            val savedSong = songRepository.saveSongFromUri(uriString)

            if (savedSong != null) {
                playerContext.addSong(savedSong)
                playerContext.loadSong(savedSong)
                playerContext.play()
            }
        }
    }

    fun onPlayPauseClick() = playerContext.play()
    fun onNextClick() = playerContext.next()
    fun onPrevClick() = playerContext.prev()
    fun onSeekClick(timestampMs: Float) = playerContext.seek(timestampMs)
    fun onLockClick() = playerContext.lock()

    fun onSongSelected(uriString: String) {
        val selectedSong = uiState.value.songList.first { it.uriString == uriString }
        playerContext.loadSong(selectedSong)
    }

    override fun onCleared() {
        super.onCleared()
        playerContext.release()
    }
}