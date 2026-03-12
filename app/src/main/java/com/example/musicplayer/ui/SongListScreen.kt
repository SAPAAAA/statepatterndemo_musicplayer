package com.example.musicplayer.ui

import android.text.format.DateUtils
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musicplayer.R
import com.example.musicplayer.domain.SongMetadata
import com.example.musicplayer.ui.components.PlaybackControls
import com.example.musicplayer.ui.theme.MusicPlayerTheme

@Composable
fun SongListScreen(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState,
    onSongSelected: (String) -> Unit,
    onAddSongClick: () -> Unit,
    playerActions: PlayerActions,
    navigateToNowPlaying: () -> Unit = { }
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier
            .weight(1f)
            .fillMaxHeight()) {
            LazyColumn(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_padding)),
                contentPadding = PaddingValues(dimensionResource(R.dimen.small_padding))
            ) {
                items(uiState.songList) { song ->
                    SongItemCard(
                        modifier = Modifier,
                        onSongSelected = onSongSelected,
                        song = song,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onAddSongClick
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier
                        .size(64.dp),
                    imageVector = Icons.Default.AddCircle, contentDescription = stringResource(R.string.add_song),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (uiState.songList.isEmpty()) "${stringResource(R.string.empty_list)}\n${stringResource(R.string.tap_to_add_a_song)}"
                    else stringResource(R.string.tap_to_add_a_song),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
        MiniPlayerBar(
            modifier = Modifier,
            uiState = uiState,
            playerActions = playerActions,
            navigateToNowPlaying = navigateToNowPlaying
        )
    }

}

@Suppress("D")
@Composable
fun MiniPlayerBar(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState,
    playerActions: PlayerActions,
    navigateToNowPlaying: () -> Unit = { }
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Only show up if a song is loaded into the media player
        if (uiState.currentSong != null) {
            var sliderPosition by remember { mutableFloatStateOf(uiState.currentTimestampMs!!) }
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered by interactionSource.collectIsHoveredAsState()
            val isDragged by interactionSource.collectIsDraggedAsState()

            val thumbSize by animateDpAsState(
                targetValue = if (isDragged || isHovered) dimensionResource(R.dimen.thumb_drag_size) else dimensionResource(R.dimen.thumb_normal_size),
                animationSpec = tween(durationMillis = 150),
                label = "thumbSize"
            )

            val haloSize by animateDpAsState(
                targetValue = if (isHovered || isDragged) 24.dp else 0.dp,
                label = "haloSize"
            )
            val haloAlpha by animateFloatAsState(
                targetValue = if (isHovered || isDragged) 0.2f else 0f,
                label = "haloAlpha"
            )

            LaunchedEffect(uiState.currentTimestampMs) {
                if (!isDragged) {
                    sliderPosition = uiState.currentTimestampMs!!
                }
            }

            // Progress Slider
            @OptIn(ExperimentalMaterial3Api::class)
            Slider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.slider_height)),
                value = sliderPosition,
                valueRange = 0f..uiState.currentSong.duration,
                interactionSource = interactionSource,
                onValueChange = { newValue ->
                    sliderPosition = newValue
                },
                onValueChangeFinished = {
                    playerActions.onSeekClick(sliderPosition)
                },
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .width(0.dp)
                            .height(16.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {}
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .requiredSize(haloSize)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = haloAlpha),
                                    shape = CircleShape
                                )
                        )
                        Box(
                            modifier = Modifier
                                .requiredSize(thumbSize)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                    }
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier
                            .height(dimensionResource(R.dimen.track_height))
                            .padding(0.dp),
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        drawStopIndicator = null,
                        thumbTrackGapSize = 0.dp,
                        trackInsideCornerSize = 0.dp
                    )
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (uiState.currentSong != null) {
                            navigateToNowPlaying()
                        }
                    }
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.currentSong != null) {
                AsyncImage(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.medium_thumbnail_image_size)),
                    model = uiState.currentSong.artFilePath,
                    contentDescription = "${uiState.currentSong.title} by ${uiState.currentSong.artist}",
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = dimensionResource(R.dimen.small_padding))
                ) {
                    Text(
                        text = uiState.currentSong.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
            }

            PlaybackControls(
                isPlaying = uiState.isPlaying,
                onPlayPauseClick = playerActions.onPlayPauseClick,
                onNextClick = playerActions.onNextClick,
                onPrevClick = playerActions.onPrevClick,
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.small_padding)),
                baseSize = dimensionResource(R.dimen.mini_player_control_base_size),
                horizontalArrangement = Arrangement.End,
                isSkipEnabled = uiState.songList.size > 1 && uiState.currentSong != null,
                isPlayEnabled = uiState.songList.isNotEmpty(),
            )
        }

    }
}

@Composable
fun SongItemCard(
    modifier: Modifier = Modifier,
    onSongSelected: (String) -> Unit,
    song: SongMetadata
) {
    Card(
        modifier = modifier
            .clickable{ onSongSelected(song.uriString) },
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            pressedElevation = dimensionResource(R.dimen.elevation_press)
        )
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.large_thumbnail_image_size)),
                model = song.artFilePath,
                contentDescription = "${song.title} by ${song.artist}",
                contentScale = ContentScale.Crop

            )
            Spacer(
                modifier = Modifier
                    .width(dimensionResource(R.dimen.medium_padding))
            )
            Column(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(R.dimen.small_padding),
                        bottom = dimensionResource(R.dimen.small_padding),
                        end = dimensionResource(R.dimen.small_padding),
                    )
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_padding))
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 1
                )
                Text(
                    text = formatTimestamp(song.duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 1,
                )
            }
        }
    }
}

private fun formatTimestamp(timestampMs: Float): String {
    val seconds = timestampMs / 1000
    return DateUtils.formatElapsedTime(seconds.toLong())
}

@Preview
@Composable
fun SongListScreenPreview() {
    MusicPlayerTheme(darkTheme = true) {
        SongItemCard(
            onSongSelected = {},
            song = SongMetadata(
                uriString = "",
                title = "YES",
                artist = "hi",
                duration = 150000F,
                artFilePath = "",
            )
        )
    }
}