package com.kevindev.animeapp.feature.player

import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.kevindev.animeapp.core.ui.theme.AnimeBg
import com.kevindev.animeapp.core.ui.theme.AnimeOrange
import com.kevindev.animeapp.feature.player.media.ExoPlayerManager
import com.kevindev.animeapp.feature.player.mvi.PlayerEffect
import com.kevindev.animeapp.feature.player.mvi.PlayerIntent
import com.kevindev.animeapp.feature.player.mvi.PlayerState
import kotlinx.coroutines.flow.collectLatest
import okhttp3.OkHttpClient

@Composable
fun PlayerScreen(
    animeId: Int,
    episodeId: String,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val exoManager = remember {
        ExoPlayerManager(context, OkHttpClient())
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(PlayerIntent.Load(episodeId, animeId))
    }

    LaunchedEffect(state) {
        val s = state as? PlayerState.Ready ?: return@LaunchedEffect
        if (s.isPlaying) exoManager.play() else exoManager.pause()
        exoManager.setSpeed(s.playbackSpeed)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is PlayerEffect.NavigateBack -> onBack()
                is PlayerEffect.SeekPlayer -> exoManager.seekTo(effect.positionMs)
                is PlayerEffect.ShowError -> Unit
            }
        }
    }

    // Sincroniza posición del player → ViewModel cada vez que cambia
    DisposableEffect(exoManager) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                viewModel.updatePosition(
                    positionMs = exoManager.player.currentPosition,
                    durationMs = exoManager.player.duration.coerceAtLeast(0),
                    bufferedMs = exoManager.player.bufferedPosition,
                )
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                viewModel.updatePosition(
                    positionMs = exoManager.player.currentPosition,
                    durationMs = exoManager.player.duration.coerceAtLeast(0),
                    bufferedMs = exoManager.player.bufferedPosition,
                )
            }
        }
        exoManager.player.addListener(listener)
        onDispose {
            exoManager.player.removeListener(listener)
            exoManager.release()
        }
    }

    LaunchedEffect(state) {
        val s = state as? PlayerState.Ready ?: return@LaunchedEffect
        if (exoManager.player.currentMediaItem == null) {
            exoManager.prepare(s.streamingInfo, s.positionMs)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoManager.player
                    useController = false
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        when (val s = state) {
            is PlayerState.Loading -> {
                CircularProgressIndicator(
                    color = AnimeOrange,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            is PlayerState.Error -> {
                Text(
                    text = s.message,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            is PlayerState.Ready -> {
                PlayerControls(
                    state = s,
                    onIntent = viewModel::onIntent,
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun PlayerControls(
    state: PlayerState.Ready,
    onIntent: (PlayerIntent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onIntent(PlayerIntent.ToggleControls) },
    ) {
        AnimatedVisibility(
            visible = state.showControls,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
            ) {
                // Botón volver
                IconButton(
                    onClick = { onIntent(PlayerIntent.Back) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                    )
                }

                // Controles centrales
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { onIntent(PlayerIntent.SeekTo(state.positionMs - 10_000)) }) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "-10s",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(AnimeOrange.copy(alpha = 0.85f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconButton(
                            onClick = {
                                if (state.isPlaying) onIntent(PlayerIntent.Pause)
                                else onIntent(PlayerIntent.Play)
                            },
                        ) {
                            Icon(
                                imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (state.isPlaying) "Pausar" else "Reproducir",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    }

                    IconButton(onClick = { onIntent(PlayerIntent.SeekTo(state.positionMs + 10_000)) }) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "+10s",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }

                // Bottom bar: seekbar + tiempo
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    if (state.showSkipIntro) {
                        SkipButton(label = "Saltar intro") { onIntent(PlayerIntent.SkipIntro) }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (state.showSkipOutro) {
                        SkipButton(label = "Saltar outro") { onIntent(PlayerIntent.SkipOutro) }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Slider(
                        value = state.progressFraction,
                        onValueChange = { fraction ->
                            onIntent(PlayerIntent.SeekTo((fraction * state.durationMs).toLong()))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = AnimeOrange,
                            activeTrackColor = AnimeOrange,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                        ),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = state.positionMs.toTimeString(),
                            color = Color.White,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = state.durationMs.toTimeString(),
                            color = Color.Gray,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkipButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(AnimeOrange, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(text = label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun Long.toTimeString(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds)
    else "%02d:%02d".format(minutes, seconds)
}
