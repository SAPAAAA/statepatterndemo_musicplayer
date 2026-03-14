package com.example.musicplayer.domain.playback

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AndroidAudioPlayer(private val context: Context) : AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    private val playerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressTrackerJob: Job? = null

    private val _currentTimestampMs = MutableStateFlow(0F)
    override val currentTimestampMs: StateFlow<Float>
        get() = _currentTimestampMs.asStateFlow()

    private val _onPlaybackCompleted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val onPlaybackCompleted: SharedFlow<Unit>
        get() = _onPlaybackCompleted.asSharedFlow()

    override fun start(uriString: String) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, uriString.toUri())
                    prepare()

                    setOnCompletionListener {
                        stopProgressTracker()
                        _onPlaybackCompleted.tryEmit(Unit)

                    }
                }
            }
            mediaPlayer?.start()
            startProgressTracker()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        stopProgressTracker()
    }

    override fun stop() {
        stopProgressTracker()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        _currentTimestampMs.value = 0F
    }

    override fun seekTo(position: Float) {
        mediaPlayer?.seekTo(position.toInt())
        _currentTimestampMs.value = position
    }

    override fun release() {
        stop()
        playerScope.cancel()
    }

    private fun startProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = playerScope.launch {
            while (isActive) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        _currentTimestampMs.value = it.currentPosition.toFloat()
                    }
                }
                delay(500L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = null
    }
}