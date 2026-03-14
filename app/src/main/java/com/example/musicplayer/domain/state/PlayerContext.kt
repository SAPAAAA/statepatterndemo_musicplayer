package com.example.musicplayer.domain.state

import com.example.musicplayer.domain.playback.AudioPlayer
import com.example.musicplayer.domain.model.SongMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerContext(private val playerAudioPlayer: AudioPlayer) {
    private val _currentState = MutableStateFlow<PlayerState>(IdleState(this))
    val currentState = _currentState.asStateFlow()

    private val contextScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _currentSong = MutableStateFlow<SongMetadata?>(null)
    val currentSong = _currentSong.asStateFlow()

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
        _currentState.value = state
    }

    fun play() = this._currentState.value.onPlay()
    fun next() = this._currentState.value.onNext()
    fun prev() = this._currentState.value.onPrev()
    fun lock() = this._currentState.value.onLock()
    fun seek(timestampMs: Float) = this._currentState.value.onSeek(timestampMs)

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

    fun loadRandomSong() {
        val randomSong = currentSongList.value.random()
        loadSong(randomSong)
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
