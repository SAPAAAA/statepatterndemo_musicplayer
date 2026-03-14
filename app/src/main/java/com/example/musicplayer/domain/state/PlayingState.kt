package com.example.musicplayer.domain.state

class PlayingState : PlayerState {
    constructor(context: PlayerContext) : super(context)

    override fun onPlay() {
        playerContext.pauseAudio()
        playerContext.transitionTo(PausedState(playerContext))
    }

    override fun onNext() {
        playerContext.nextTrack()
        playerContext.startAudio()
    }

    override fun onPrev() {
        playerContext.prevTrack()
        playerContext.startAudio()
        playerContext.transitionTo(PlayingState(playerContext))
    }

    override fun onSeek(timestampMs: Float) {
        playerContext.seekTo(timestampMs)
    }

    override fun onLock() {
        playerContext.transitionTo(LockedState(playerContext, this))
    }
}