package com.example.musicplayer.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayer.domain.playback.AndroidAudioPlayer
import com.example.musicplayer.domain.state.PlayerContext
import com.example.musicplayer.domain.repository.SongRepositoryImpl

class PlayerViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    private val playerContext = PlayerContext(AndroidAudioPlayer(context))

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            val songRepository = SongRepositoryImpl(context)

            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(playerContext, songRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}