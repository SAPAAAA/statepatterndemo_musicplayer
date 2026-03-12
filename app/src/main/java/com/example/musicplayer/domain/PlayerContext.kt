package com.example.musicplayer.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerContext(private val playerAudioPlayer: AudioPlayer) {
    private var playerState: PlayerState = IdleState(this)
    private val contextScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _currentSong = MutableStateFlow<SongMetadata?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _currentState = MutableStateFlow(SimpleState.IDLE)
    val currentState = _currentState.asStateFlow()

    val currentTimestampMs = playerAudioPlayer.currentTimestampMs
    val isPlaying = playerAudioPlayer.isPlaying

    private val _currentSongList = MutableStateFlow<List<SongMetadata>>(emptyList())
    val currentSongList = _currentSongList.asStateFlow()

    init {
        contextScope.launch {
            playerAudioPlayer.onPlaybackCompleted.collect {
                next()
            }
        }
    }

    fun transitionTo(state: PlayerState) {
        playerState = state
        _currentState.value = when (state) {
            is IdleState -> SimpleState.IDLE
            is PlayingState -> SimpleState.PLAYING
            is PausedState -> SimpleState.PAUSED
            is LockedState -> SimpleState.LOCKED
            else -> SimpleState.IDLE
        }
    }

    fun play() = this.playerState.onPlay()
    fun next() = this.playerState.onNext()
    fun prev() = this.playerState.onPrev()
    fun lock() = this.playerState.onLock()
    fun seek(timestampMs: Float) = this.playerState.onSeek(timestampMs)

    fun startAudio() {
        val song = _currentSong.value ?: return
        playerAudioPlayer.start(song.uriString)
    }

    fun seekTo(timestampMs: Float) {

        playerAudioPlayer.seekTo(timestampMs)
    }

    fun pauseAudio() {
        playerAudioPlayer.pause()
    }

    fun stopAudio() {
        playerAudioPlayer.stop()
    }

    fun loadSong(song: SongMetadata) {
        stopAudio()
        _currentSong.value = song
        transitionTo(IdleState(this))
        play()
    }

    fun nextTrack() {
        val list = _currentSongList.value
        if (list.isEmpty()) return
        val index = list.indexOf(_currentSong.value)

        // Out of bound check
        this._currentSong.value = if (index != -1 && index + 1 < list.size) {
            list[index + 1]
        } else {
            list.first()
        }

        stopAudio()
    }

    fun addSong(song: SongMetadata) {
        _currentSongList.update { currentList ->
            currentList + song
        }
    }

    fun prevTrack() {
        val list = _currentSongList.value
        if (list.isEmpty()) return
        val index = list.indexOf(_currentSong.value)

        this._currentSong.value = if (index > 0) {
            list[index - 1]
        } else {
            list.first()
        }
        stopAudio()
    }

    fun addSongs(songs: List<SongMetadata>) {
        _currentSongList.update { currentList ->
            currentList + songs
        }
    }

    fun release() {
        playerAudioPlayer.release()
        contextScope.cancel()
    }
}
