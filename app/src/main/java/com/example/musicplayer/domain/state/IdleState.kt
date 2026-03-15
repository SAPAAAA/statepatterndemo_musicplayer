package com.example.musicplayer.domain.state

class IdleState : PlayerState {
    constructor(context: PlayerContext) : super(context)

    override val isPlaying: Boolean = false

    override fun getType(): SimpleState = SimpleState.IDLE

    override fun onPlay() {
        if (playerContext.currentSongList.value.isNotEmpty()) {
            playerContext.loadRandomSong()
        }
        playerContext.startAudio()
        playerContext.transitionTo(PlayingState(playerContext))

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
        playerContext.transitionTo(LockedState(playerContext, this))
    }
    override fun onPlaybackCompleted() {
        /* Do nothing */
    }
}