package com.applicaster.liveswitcher.utils

import com.applicaster.atom.model.APAtomEntry
import com.applicaster.liveswitcher.model.ProgramModel
import com.applicaster.player.VideoAdsUtil
import com.applicaster.plugin_manager.playersmanager.AdsConfiguration
import com.applicaster.plugin_manager.playersmanager.Playable
import com.applicaster.plugin_manager.playersmanager.PlayableConfiguration
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LiveSwitcherUtil {
    companion object {
        fun getProgramsFromAtomEntries(entries: ArrayList<APAtomEntry>)
                : ArrayList<ProgramModel.Program> {
            val programs = ArrayList<ProgramModel.Program>()
            entries.forEach {
                programs.add(ProgramModel.Program(
                        it.title,
                        it.extensions?.get("start_time").toString(),
                        it.extensions?.get("end_time").toString(),
                        it.extensions?.get("applicaster_channel_id").toString(),
                        it.mediaGroups[0].mediaItems[0].src,
                        it.title,
                        it.summary,
                        it.content.src))
            }

            return programs
        }

        fun getLiveAtoms(entries: ArrayList<APAtomEntry>): List<APAtomEntry> {
            val liveAtoms = ArrayList<APAtomEntry>()
            entries.forEach {
                if(getDateFormatted(it.extensions?.get("start_time").toString()) < getCurrentDate()
                        && getCurrentDate() < getDateFormatted(it.extensions?.get("end_time").toString())) {
                    liveAtoms.add(it)
                }
            }
            return liveAtoms
        }

        fun getNextAtoms(entries: ArrayList<APAtomEntry>): List<APAtomEntry> {
            val nextAtoms = ArrayList<APAtomEntry>()
            entries.forEach {
                if(getDateFormatted(it.extensions?.get("start_time").toString()) > getCurrentDate()
                        && getCurrentDate() < getDateFormatted(it.extensions?.get("end_time").toString())) {
                    nextAtoms.add(it)
                }
            }

            return nextAtoms
        }

        fun getConfigurationFromPlayable(playable: Playable): PlayableConfiguration {
            var configuration = PlayableConfiguration()
            val adsConfiguration = AdsConfiguration()
            adsConfiguration.extensionName = VideoAdsUtil.getPrerollExtension(playable.isLive, true)
            configuration.adsConfiguration = adsConfiguration
            return configuration
        }

        fun getCurrentDate(): String {
            val date = Date()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            return dateFormat.format(date)
        }

        fun getDateFormatted(date: String): String {
            val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            sourceFormat.timeZone = TimeZone.getTimeZone("GMT-3")
            val parsed = sourceFormat.parse(date)

            val tz = TimeZone.getDefault()
            val destFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            destFormat.timeZone = tz

            return destFormat.format(parsed)
        }
    }
}
