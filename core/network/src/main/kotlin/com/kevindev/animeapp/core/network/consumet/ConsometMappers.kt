package com.kevindev.animeapp.core.network.consumet

import com.kevindev.animeapp.core.model.Episode
import com.kevindev.animeapp.core.model.EpisodeStreamingInfo
import com.kevindev.animeapp.core.model.StreamingSource
import com.kevindev.animeapp.core.model.SubtitleTrack

fun ConsometEpisodeDto.toEpisode(animeId: Int) = Episode(
    id = id,
    animeId = animeId,
    number = number,
    title = title,
    description = null,
    thumbnail = null,
    isFiller = isFiller,
    airDate = null,
)

fun ConsometWatchResponseDto.toStreamingInfo() = EpisodeStreamingInfo(
    sources = sources.map {
        StreamingSource(url = it.url, quality = it.quality, isM3U8 = it.isM3U8)
    },
    subtitles = subtitles
        .filter { it.kind == null || it.kind == "captions" }
        .map {
            SubtitleTrack(
                url = it.url,
                lang = it.lang,
                label = it.label ?: it.lang,
                kind = it.kind ?: "captions",
            )
        },
)
