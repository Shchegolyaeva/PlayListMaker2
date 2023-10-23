package com.practicum.playlistmaker

import com.practicum.playlistmaker.audioplayer.domain.Track

data class ItunesResponse(val results: List<Track>)