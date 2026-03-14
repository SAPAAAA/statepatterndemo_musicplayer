package com.example.musicplayer.domain.state

class PausedState : PlayerState {
    constructor(context: PlayerContext) : super(context)

    override fun onPlay() {
        playerContext.startAudio()
        playerContext.transitionTo(PlayingState(playerContext))
    }

    override fun onNext() {
        playerContext.nextTrack()
        playerContext.startAudio()
        playerContext.transitionTo(PlayingState(playerContext))
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