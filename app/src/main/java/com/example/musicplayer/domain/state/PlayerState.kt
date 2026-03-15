package com.example.musicplayer.domain.state

abstract class PlayerState {
    protected var playerContext: PlayerContext

    protected constructor(context: PlayerContext) {
        this.playerContext = context
    }

    abstract val isPlaying: Boolean

    abstract fun getType(): SimpleState
    abstract fun onNext()
    abstract fun onPrev()
    abstract fun onPlay()
    abstract fun onLock()
    abstract fun onSeek(timestampMs: Float)
    abstract fun onPlaybackCompleted()
}