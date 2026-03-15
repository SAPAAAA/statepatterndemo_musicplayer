package com.example.musicplayer.domain.state

class LockedState : PlayerState {
    private val prevState: PlayerState
    constructor(context: PlayerContext, prevState: PlayerState) : super(context) {
        this.prevState = prevState
    }

    override val isPlaying: Boolean
        get() = prevState.isPlaying

    override fun getType(): SimpleState = SimpleState.LOCKED

    override fun onPlay() {
        /* Do nothing */
    }
    override fun onNext() {
        /* Do nothing */
    }
    override fun onPrev() {
        /* Do nothing */
    }
    override fun onSeek(timestampMs: Float) {
        /* Do nothing */
    }
    override fun onLock() {
        playerContext.transitionTo(prevState)
    }
    override fun onPlaybackCompleted() {
        this.prevState.onNext()
    }
}