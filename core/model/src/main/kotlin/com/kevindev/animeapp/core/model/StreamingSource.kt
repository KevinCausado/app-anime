package com.kevindev.animeapp.core.model

data class StreamingSource(
    val url: String,
    val quality: String,
    val isM3U8: Boolean,
)

data class SubtitleTrack(
    val url: String,
    val lang: String,
    val label: String,
    val kind: String,
)

data class EpisodeStreamingInfo(
    val sources: List<StreamingSource>,
    val subtitles: List<SubtitleTrack>,
)
