package com.example.musicplayer.domain.model

data class SongMetadata(
    val uriString: String,
    val title: String,
    val artist: String,
    val duration: Float,
    val artFilePath: String,
)

interface SongRepository{
    suspend fun saveSongFromUri(uriString: String): SongMetadata?
    suspend fun getSong(uriString: String): SongMetadata
    suspend fun getAllSongs(): List<SongMetadata>
}


