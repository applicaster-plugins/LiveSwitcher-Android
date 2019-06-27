package com.applicaster.liveswitcher.model

object ProgramModel {
    data class Program(val name: String, val startTime: String, val endTime: String,
                       val channelId: String, val coverImg: String, val title: String,
                       val summary: String)
}
