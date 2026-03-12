package com.example.musicplayer.domain

import android.content.Context
import android.media.MediaMetadataRetriever
import com.google.gson.Gson
import androidx.core.net.toUri
import java.io.File
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongRepositoryImpl(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : SongRepository {
    private val prefs = context.getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    override suspend fun saveSongFromUri(uriString: String): SongMetadata? = withContext(dispatcher) {
        val uri = uriString.toUri()
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(context, uri)

            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown Title"
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"

            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: "0"
            val duration = durationStr.toFloat()

            val artBytes = retriever.embeddedPicture
            var savedArtPath = ""

            if (artBytes != null) {
                val fileName = "art_${uriString.hashCode()}.jpg"
                val file = File(context.filesDir, fileName)
                file.writeBytes(artBytes)
                savedArtPath = file.absolutePath
            }

            val newSong = SongMetadata(
                uriString = uriString,
                title = title,
                artist = artist,
                duration = duration,
                artFilePath = savedArtPath
            )

            prefs.edit { putString(uriString, gson.toJson(newSong)) }

            return@withContext newSong
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            retriever.release()
        }
    }

    override suspend fun getSong(uriString: String): SongMetadata = withContext(dispatcher) {
        val jsonString = prefs.getString(uriString, null)

        if (jsonString != null) {
            return@withContext gson.fromJson(jsonString, SongMetadata::class.java)
        } else {
            throw IllegalArgumentException("Song metadata not found for URI: $uriString")
        }
    }

    override suspend fun getAllSongs(): List<SongMetadata> = withContext(dispatcher) {
        val allEntries = prefs.all
        val songList = mutableListOf<SongMetadata>()

        for ((_, value) in allEntries) {
            if (value is String) {
                try {
                    val song = gson.fromJson(value, SongMetadata::class.java)
                    songList.add(song)
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }

        return@withContext songList
    }
}