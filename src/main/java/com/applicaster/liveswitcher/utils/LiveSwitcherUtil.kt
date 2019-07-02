package com.applicaster.liveswitcher.utils

import com.applicaster.atom.model.APAtomEntry
import com.applicaster.liveswitcher.model.ChannelModel
import com.applicaster.liveswitcher.model.ProgramModel
import com.applicaster.player.VideoAdsUtil
import com.applicaster.plugin_manager.playersmanager.AdsConfiguration
import com.applicaster.plugin_manager.playersmanager.Playable
import com.applicaster.plugin_manager.playersmanager.PlayableConfiguration
import com.google.gson.internal.LinkedTreeMap
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.text.ParseException


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

        fun getLiveAtoms(entries: List<APAtomEntry>?): List<APAtomEntry> {
            val liveAtoms = ArrayList<APAtomEntry>()
            entries?.forEach {
                if (getDateFormatted(it.extensions?.get("start_time").toString()) < getCurrentDate()
                        && getCurrentDate() < getDateFormatted(it.extensions?.get("end_time").toString())) {
                    liveAtoms.add(it)
                }
            }
            return liveAtoms
        }

        fun getNextAtoms(entries: List<APAtomEntry>?): List<APAtomEntry> {
            val nextAtoms = ArrayList<APAtomEntry>()
            entries?.forEach {
                if (getDateFormatted(it.extensions?.get("start_time").toString()) > getCurrentDate()
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
            sourceFormat.timeZone = TimeZone.getTimeZone("GMT-5")
            val parsed = sourceFormat.parse(date)

            val destFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            destFormat.timeZone = TimeZone.getDefault()

            return destFormat.format(parsed)
        }

        fun getTimeField(startTime: String, endTime: String): String {
            val start = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val end = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            start.timeZone = TimeZone.getTimeZone("GMT-5")
            end.timeZone = TimeZone.getTimeZone("GMT-5")

            val startDate = start.parse(startTime)
            val endDate = end.parse(endTime)

            val startDateFormatter = SimpleDateFormat("dd MMMM, HH:mm", Locale.getDefault())
            val endDateFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            startDateFormatter.timeZone = TimeZone.getDefault()
            endDateFormatter.timeZone = TimeZone.getDefault()

            return String.format("%s - %s", startDateFormatter.format(startDate),
                    endDateFormatter.format(endDate))
        }

        fun getChannelsFromAtom(extensions: Map<String, Any>): List<ChannelModel.Channel> {
            var channels = extensions["channels"]
            var channelsFromAtom = ArrayList<ChannelModel.Channel>()
            if (channels is ArrayList<*>) {
                channels.forEach {
                    if (it is LinkedTreeMap<*, *>) {
                        channelsFromAtom.add(ChannelModel.Channel(
                                it["name"].toString(),
                                it["id"].toString(),
                                it["logo"].toString(),
                                it["free"].toString().toBoolean(),
                                it["epg"].toString().toBoolean()))
                    }
                }
            }

            return channelsFromAtom
        }

        fun getChannelIconUrl(channels: List<ChannelModel.Channel>?, applicasterId: String): String {
            channels?.forEach {
                if(it.id == applicasterId) {
                    return it.logo
                }
            }

            return ""
        }

        fun getDateInMillis(startTime: String): Long {
            val start = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            start.timeZone = TimeZone.getTimeZone("GMT-5")

            val startDate = start.parse(startTime)

            val startDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            startDateFormatter.timeZone = TimeZone.getDefault()

            val startDateString = startDateFormatter.format(startDate)
            val starDateDate = startDateFormatter.parse(startDateString)

            try {
                return starDateDate.time // return time in milliseconds
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return 0
        }
    }
}
