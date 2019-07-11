package com.applicaster.liveswitcher.screen

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applicaster.atom.model.APAtomEntry
import com.applicaster.lesscodeutils.ui.AlertUtils.Companion.getAlertDialog
import com.applicaster.liveswitcher.R
import com.applicaster.liveswitcher.model.ChannelModel
import com.applicaster.liveswitcher.screen.adapter.ProgramAdapter
import com.applicaster.liveswitcher.screen.base.HeartbeatFragment
import com.applicaster.liveswitcher.utils.Constants.CONF_BACKGROUND_COLOR
import com.applicaster.liveswitcher.utils.Constants.CONF_LIVE_HEADER_BACKGROUND_COLOR
import com.applicaster.liveswitcher.utils.Constants.CONF_LIVE_HEADER_FONTSIZE
import com.applicaster.liveswitcher.utils.Constants.CONF_LIVE_HEADER_TEXT
import com.applicaster.liveswitcher.utils.Constants.CONF_LIVE_HEADER_TEXT_COLOR
import com.applicaster.liveswitcher.utils.Constants.CONF_NEXT_HEADER_BACKGROUND_COLOR
import com.applicaster.liveswitcher.utils.Constants.CONF_NEXT_HEADER_FONTSIZE
import com.applicaster.liveswitcher.utils.Constants.CONF_NEXT_HEADER_TEXT
import com.applicaster.liveswitcher.utils.Constants.CONF_NEXT_HEADER_TEXT_COLOR
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_APPLICASTER_CHANNEL_ID
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_END_TIME
import com.applicaster.liveswitcher.utils.Constants.EXTENSION_START_TIME
import com.applicaster.liveswitcher.utils.Constants.PREFERENCE_ITEM_SELECTED_POSITION
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil.Companion.getChannelsFromAtom
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil.Companion.getConfigurationFromPlayable
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil.Companion.getDateInMillis
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil.Companion.getFloat
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil.Companion.getLiveAtoms
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil.Companion.getNextAtoms
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil.Companion.getParam
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil.Companion.parseColor
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil.Companion.setViewBackground
import com.applicaster.model.APChannel
import com.applicaster.model.APProgram
import com.applicaster.plugin_manager.playersmanager.PlayerContract
import com.applicaster.plugin_manager.playersmanager.internal.PlayersManager
import com.applicaster.util.AlarmManagerUtil
import com.applicaster.util.OSUtil
import com.applicaster.util.PreferenceUtil
import com.applicaster.util.serialization.SerializationUtils
import com.applicaster.util.ui.ImageHolderBuilder
import kotlinx.android.synthetic.main.fragment_live_switcher.*

class LiveSwitcherFragment : HeartbeatFragment(), LiveSwitcherView, ProgramAdapter.OnProgramClickListener {

    var data: Any? = null
    var entries: List<APAtomEntry>? = null
    var channels: List<ChannelModel.Channel>? = null
    var playerContract: PlayerContract? = null
    var currentAtomEntry: APAtomEntry? = null
    var liveSwitcherPresenter: LiveSwitcherPresenter = LiveSwitcherPresenter(this,
            LiveSwitcherInteractor())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_live_switcher, container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set up view
        setUpView()

        // data has value when creating the fragment
        liveSwitcherPresenter.getAtoms(data)
    }

    private fun setUpView() {
        cl_live_switcher.setBackgroundColor(parseColor(getParam(CONF_BACKGROUND_COLOR)))

        tv_header_live.text = getParam(CONF_LIVE_HEADER_TEXT)
        tv_header_live.setTextColor(parseColor(getParam(CONF_LIVE_HEADER_TEXT_COLOR)))
        tv_header_live.textSize = getFloat(getParam(CONF_LIVE_HEADER_FONTSIZE))
        // v_header_live is only showed in tablet
        v_header_live?.setBackgroundColor(parseColor(getParam(CONF_LIVE_HEADER_BACKGROUND_COLOR)))

        setViewBackground(tv_header_live, CONF_LIVE_HEADER_BACKGROUND_COLOR)

        tv_header_next.text = getParam(CONF_NEXT_HEADER_TEXT)
        tv_header_next.setTextColor(parseColor(getParam(CONF_NEXT_HEADER_TEXT_COLOR)))
        tv_header_next.textSize = getFloat(getParam(CONF_NEXT_HEADER_FONTSIZE))
        // v_header_next is only showed in tablet
        v_header_next?.setBackgroundColor(parseColor(getParam(CONF_NEXT_HEADER_BACKGROUND_COLOR)))

        setViewBackground(tv_header_next, CONF_NEXT_HEADER_BACKGROUND_COLOR)
    }

    override fun showProgress() {
        pb_loading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        pb_loading.visibility = View.GONE
        // live header
        tv_header_live.visibility = View.VISIBLE
        v_header_live?.visibility = View.VISIBLE
        // next header
        tv_header_next.visibility = View.VISIBLE
        v_header_next?.visibility = View.VISIBLE
    }

    override fun onAtomsFetchedSuccessfully(atomEntries: List<APAtomEntry>, extensions: Map<String, Any>) {
        // save globally the entries
        entries = atomEntries
        // get channels to get the logo later
        channels = getChannelsFromAtom(extensions)

        // recycler view with the live content
        val liveItems = getLiveAtoms(entries)

        setUpRecyclerView(rv_live, liveItems, channels, true)
        playFirstItem(liveItems)

        // recycler view with the content that goes next
        setUpRecyclerView(rv_next, getNextAtoms(entries), channels, false)
    }

    override fun onAtomsFetchedFail() {
        context?.let {
            getAlertDialog(it, resources.getString(R.string.error_dialog_title),
                    resources.getString(R.string.error_dialog_message), resources.getString(R.string.ok_btn),
                    DialogInterface.OnClickListener { _, _ -> liveSwitcherPresenter.getAtoms(data) }).show()
        }
    }

    private fun playFirstItem(liveItems: List<APAtomEntry>) {
        val position = PreferenceUtil.getInstance().getIntPref(PREFERENCE_ITEM_SELECTED_POSITION,
                0)

        if (position < liveItems.size) {
            onProgramClicked(liveItems[position])
        }
    }

    private fun setUpRecyclerView(recyclerView: RecyclerView?, items: List<APAtomEntry>,
                                  channels: List<ChannelModel.Channel>?,
                                  isLive: Boolean) {
        recyclerView?.let {
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = if (!OSUtil.isTablet()) LinearLayoutManager(context)
            else LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = ProgramAdapter(items, channels, context, this, isLive)
            // this enables the smooth scroll in the nested scroll view
            ViewCompat.setNestedScrollingEnabled(recyclerView, false)
        }
    }


    override fun heartbeat() {
        entries?.let {
            // recycler view with the live content
            setUpRecyclerView(rv_live, getLiveAtoms(entries), channels, true)

            // recycler view with the content that goes next
            setUpRecyclerView(rv_next, getNextAtoms(entries), channels, false)
        }
    }

    override fun onProgramClicked(atomEntry: APAtomEntry) {
        // saving the current atom entry to manage it when the app goes to pause mode and then resumes
        // (see onPause() and onResume() methods)
        this.currentAtomEntry = atomEntry
        val playersManager = PlayersManager.getInstance()
        val channelId = atomEntry.extensions?.get(EXTENSION_APPLICASTER_CHANNEL_ID)

        playerContract?.removeInline(rl_player)

        channelId?.let {
            val apChannel = APChannel()
            apChannel.id = atomEntry.extensions?.get(EXTENSION_APPLICASTER_CHANNEL_ID).toString()
            playerContract = playersManager.createPlayer(apChannel, context)
        } ?: run {
            playerContract = playersManager.createPlayer(atomEntry.playable, context)
        }

        playerContract?.let {
            it.attachInline(rl_player)
            it.playInline(getConfigurationFromPlayable(atomEntry.playable))
        }
    }

    override fun onPause() {
        super.onPause()
        playerContract?.stopInline()
    }

    override fun onResume() {
        super.onResume()
        currentAtomEntry?.let {
            playerContract?.playInline(getConfigurationFromPlayable(it.playable))
        }
    }

    override fun addReminder(atomEntry: APAtomEntry) {
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
            AlarmManagerUtil.addProgramToReminder(it, atomEntry.id, programAsJson, getDateInMillis(program.starts_at))
        }
    }

    override fun removeReminder(atomEntry: APAtomEntry) {
        context?.let {
            AlarmManagerUtil.removeIfExistsInReminder(it, atomEntry.id)
        }
    }
}
