package com.example.musicplayer.domain

class IdleState : PlayerState {
    constructor(context: PlayerContext) : super(context)

    override fun onPlay() {
        if (playerContext.currentSong.value != null) {
            playerContext.startAudio()
            playerContext.transitionTo(PlayingState(playerContext))
        } else {
            if (playerContext.currentSongList.value.isNotEmpty()) {
                playerContext.loadRandomSong()
            }
        }
    }
    override fun onNext() {}
    override fun onPrev() {}
    override fun onSeek(timestampMs: Float) {}
    override fun onLock() {
        playerContext.transitionTo(LockedState(playerContext, this))
    }
}