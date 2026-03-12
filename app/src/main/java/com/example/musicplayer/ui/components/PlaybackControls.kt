package com.example.musicplayer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    modifier: Modifier = Modifier,
    baseSize: Dp = 64.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly,
) {
    val playButtonSize = baseSize * 1.25f
    val playIconSize = baseSize
    val skipIconSize = baseSize * 0.75f

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPrevClick,
            modifier = Modifier.size(baseSize)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                contentDescription = "Skip Previous",
                modifier = Modifier.size(skipIconSize),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier.size(playButtonSize)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(playIconSize),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = onNextClick,
            modifier = Modifier.size(baseSize)
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = "Skip Next",
                modifier = Modifier.size(skipIconSize),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}