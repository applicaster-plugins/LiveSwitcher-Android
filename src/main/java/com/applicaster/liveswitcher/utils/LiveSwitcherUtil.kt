package com.applicaster.liveswitcher.utils

import com.applicaster.atom.model.APAtomEntry
import com.applicaster.liveswitcher.model.ProgramModel

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
    }
}
