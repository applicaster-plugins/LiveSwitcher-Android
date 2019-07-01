package com.applicaster.liveswitcher.model

object ChannelModel {
    data class Channel(val name: String, val id: String, val logo: String, val free: Boolean,
                            val epg: Boolean)
}