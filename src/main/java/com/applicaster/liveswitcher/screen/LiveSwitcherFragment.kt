package com.applicaster.liveswitcher.screen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applicaster.atom.model.APAtomEntry
import com.applicaster.atom.model.APAtomError
import com.applicaster.atom.model.APAtomFeed
import com.applicaster.jspipes.JSManager
import com.applicaster.liveswitcher.R
import com.applicaster.liveswitcher.model.ChannelModel
import com.applicaster.liveswitcher.screen.adapter.ProgramAdapter
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_APPLICASTER_CHANNEL_ID
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_END_TIME
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_START_TIME
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil
import com.applicaster.model.APProgram
import com.applicaster.player.VideoAdsUtil
import com.applicaster.plugin_manager.playersmanager.AdsConfiguration
import com.applicaster.plugin_manager.playersmanager.Playable
import com.applicaster.plugin_manager.playersmanager.PlayableConfiguration
import com.applicaster.plugin_manager.playersmanager.internal.PlayersManager
import com.applicaster.util.AlarmManagerUtil
import com.applicaster.util.DateUtil
import com.applicaster.util.serialization.SerializationUtils
import com.applicaster.util.ui.ImageHolderBuilder
import com.applicaster.util.ui.Toaster
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_live_switcher.*

class LiveSwitcherFragment : HeartbeatFragment(), ProgramAdapter.OnProgramClickListener {

    var data: Any? = null
    var entries: List<APAtomEntry>? = null
    var channels: List<ChannelModel.Channel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_live_switcher, container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (data is LinkedTreeMap<*, *>) {
            val source = (data as LinkedTreeMap<*, *>)["source"].toString()
            Handler(Looper.getMainLooper()).post {
                JSManager.getInstance().get(source, object : JSManager.JSManagerCallback {
                    override fun onResult(atom: Any) {
                        Log.d(this.javaClass.simpleName, "onResult")
                        if (atom is APAtomFeed && atom.entries is ArrayList<APAtomEntry>) {
                            this@LiveSwitcherFragment.entries = atom.entries
                            // get channels
                            this@LiveSwitcherFragment.channels = LiveSwitcherUtil.getChannelsFromAtom(atom.extensions)

                            // recycler view with the live content
                            setUpRecyclerView(rv_live, LiveSwitcherUtil
                                    .getLiveAtoms(this@LiveSwitcherFragment.entries),
                                    this@LiveSwitcherFragment.channels, true)

                            // recycler view with the content that goes next
                            setUpRecyclerView(rv_next, LiveSwitcherUtil
                                    .getNextAtoms(this@LiveSwitcherFragment.entries),
                                    this@LiveSwitcherFragment.channels, false)
                        }
                    }

                    override fun onError(error: APAtomError) {
                        Log.d(this.javaClass.simpleName, "onError")
                    }
                })
            }
        }
    }

    fun setUpRecyclerView(recyclerView: RecyclerView?, items: List<APAtomEntry>,
                          channels: List<ChannelModel.Channel>?,
                          isLive: Boolean) {
        recyclerView?.let {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = ProgramAdapter(items, channels, context, this, isLive)
            ViewCompat.setNestedScrollingEnabled(recyclerView, false)
        }
    }


    override fun heartbeat() {
        entries?.let {
            // recycler view with the live content
            setUpRecyclerView(rv_live, LiveSwitcherUtil.getLiveAtoms(entries),
                    channels, true)

            // recycler view with the content that goes next
            setUpRecyclerView(rv_next, LiveSwitcherUtil.getNextAtoms(entries),
                    channels, false)
        }
    }

    override fun onProgramClicked(atomEntry: APAtomEntry) {
        iv_image.visibility = View.VISIBLE
        Glide.with(this@LiveSwitcherFragment)
                .asDrawable().load(atomEntry.mediaGroups[0].mediaItems[0].src).into(iv_image)
        val playersManager = PlayersManager.getInstance()
        val playerContract = playersManager.createPlayer(atomEntry.playable, context)
        if (playerContract != null) {
            playerContract.attachInline(rl_player)
            playerContract.playInline(LiveSwitcherUtil.getConfigurationFromPlayable(atomEntry.playable))
        }
    }

    override fun onReminderClicked(atomEntry: APAtomEntry) {
        val program = APProgram()
        program.id = atomEntry.id
        program.channel_id = atomEntry.extensions?.get(EXTENSION_APPLICASTER_CHANNEL_ID).toString()
        program.starts_at = atomEntry.extensions?.get(EXTENSION_START_TIME).toString()
        program.ends_at = atomEntry.extensions?.get(EXTENSION_END_TIME).toString()
        program.name = atomEntry.title
        program.is_live = atomEntry.playable.isLive.toString()

        val programImageHolder = ImageHolderBuilder.getProgramItemImageHolder(program, ImageHolderBuilder.ReminderHandlerType.NONE)
        val programAsJson = SerializationUtils.toJson(programImageHolder)

        context?.let {
            AlarmManagerUtil.addProgramToReminder(it, atomEntry.id, programAsJson, LiveSwitcherUtil.getDateInMillis(program.starts_at))
        }
    }
}
