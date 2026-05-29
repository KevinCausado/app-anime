package com.kevindev.animeapp.feature.player.media

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.kevindev.animeapp.core.model.EpisodeStreamingInfo
import com.kevindev.animeapp.core.model.SubtitleTrack
import okhttp3.OkHttpClient

class ExoPlayerManager(
    context: Context,
    okHttpClient: OkHttpClient,
) {
    val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private val dataSourceFactory = OkHttpDataSource.Factory(okHttpClient)

    fun prepare(streamingInfo: EpisodeStreamingInfo, startPositionMs: Long = 0L) {
        val source = streamingInfo.sources.firstOrNull { it.isM3U8 }
            ?: streamingInfo.sources.firstOrNull()
            ?: return

        val subtitleConfigs = streamingInfo.subtitles.map { sub ->
            MediaItem.SubtitleConfiguration.Builder(android.net.Uri.parse(sub.url))
                .setMimeType(MimeTypes.TEXT_VTT)
                .setLanguage(sub.lang)
                .setLabel(sub.label)
                .build()
        }

        val mediaItem = MediaItem.Builder()
            .setUri(source.url)
            .setSubtitleConfigurations(subtitleConfigs)
            .build()

        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)
        player.seekTo(startPositionMs)
        player.prepare()
        player.playWhenReady = true
    }

    fun play() { player.play() }
    fun pause() { player.pause() }
    fun seekTo(positionMs: Long) { player.seekTo(positionMs) }
    fun setSpeed(speed: Float) { player.playbackParameters = PlaybackParameters(speed) }

    fun selectSubtitleTrack(index: Int) {
        val params = player.trackSelectionParameters.buildUpon()
            .setPreferredTextLanguage(null)
            .build()
        player.trackSelectionParameters = params
    }

    fun release() { player.release() }
}
