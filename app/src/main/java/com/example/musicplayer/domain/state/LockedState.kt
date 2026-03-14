package com.example.musicplayer.domain.state

class LockedState : PlayerState {
    private val prevState: PlayerState
    constructor(context: PlayerContext, prevState: PlayerState) : super(context) {
        this.prevState = prevState
    }

    override fun onPlay() {}
    override fun onNext() {}
    override fun onPrev() {}
    override fun onSeek(timestampMs: Float) {}
    override fun onLock() {
        playerContext.transitionTo(prevState)
    }
}