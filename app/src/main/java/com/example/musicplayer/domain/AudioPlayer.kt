package com.example.musicplayer.domain

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val currentTimestampMs: StateFlow<Float>
    val isPlaying: StateFlow<Boolean>

    val onPlaybackCompleted: SharedFlow<Unit>

    fun start(uriString: String)
    fun pause()
    fun stop()
    fun seekTo(position: Float)
    fun release()
}