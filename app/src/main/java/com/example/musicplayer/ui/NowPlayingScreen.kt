package com.example.musicplayer.ui

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.PlaybackControls

@Composable
fun NowPlayingScreen(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState,
    playerActions: PlayerActions,
) {
    if (uiState.currentSong == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No song currently playing",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    var sliderPosition by remember { mutableFloatStateOf(uiState.currentTimestampMs!!) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.currentTimestampMs) {
        if (!isDragging) {
            sliderPosition = uiState.currentTimestampMs!!
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Large Album Art
        AsyncImage(
            model = uiState.currentSong.artFilePath,
            contentDescription = "${uiState.currentSong.title} by ${uiState.currentSong.artist}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Title and Artist
        Text(
            text = uiState.currentSong.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = uiState.currentSong.artist,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Progress Slider
        Slider(
            value = sliderPosition,
            valueRange = 0f..(if (uiState.currentSong.duration > 0f) uiState.currentSong.duration else 1f),
            onValueChange = {
                isDragging = true
                sliderPosition = it
            },
            onValueChangeFinished = {
                isDragging = false
                playerActions.onSeekClick(sliderPosition)
            },
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                thumbColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        // Timestamp Texts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatNowPlayingTimestamp(sliderPosition),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatNowPlayingTimestamp(uiState.currentSong.duration),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PlaybackControls(
            modifier = Modifier.fillMaxWidth(),
            isPlaying = uiState.isPlaying,
            onPlayPauseClick = playerActions.onPlayPauseClick,
            onNextClick = playerActions.onNextClick,
            onPrevClick = playerActions.onPrevClick,
            baseSize = dimensionResource(id = R.dimen.playback_control_base_size)
        )
    }
}

private fun formatNowPlayingTimestamp(timestampMs: Float): String {
    val seconds = timestampMs / 1000
    return DateUtils.formatElapsedTime(seconds.toLong())
}