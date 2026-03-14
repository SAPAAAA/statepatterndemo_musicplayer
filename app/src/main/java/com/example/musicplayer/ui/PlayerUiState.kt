package com.example.musicplayer.ui

import com.example.musicplayer.domain.state.SimpleState
import com.example.musicplayer.domain.model.SongMetadata

data class PlayerUiState(
    val currentSong: SongMetadata? = null,
    val currentState: SimpleState = SimpleState.IDLE,
    val isPlaying: Boolean = false,
    val currentTimestampMs: Float? = if (currentSong != null) 0F else null,
    val songList: List<SongMetadata> = emptyList()
)