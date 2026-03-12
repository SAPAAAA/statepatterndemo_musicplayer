package com.example.musicplayer.domain

abstract class PlayerState {
    protected var playerContext: PlayerContext

    protected constructor(context: PlayerContext) {
        this.playerContext = context
    }

    abstract fun onNext()
    abstract fun onPrev()
    abstract fun onPlay()
    abstract fun onLock()
    abstract fun onSeek(timestampMs: Float)
}