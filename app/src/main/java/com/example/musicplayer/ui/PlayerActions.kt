package com.example.musicplayer.ui

data class PlayerActions(
    val onPlayPauseClick: () -> Unit,
    val onNextClick: () -> Unit,
    val onPrevClick: () -> Unit,
    val onSeekClick: (Float) -> Unit,
    val onLockClick: () -> Unit,
)
