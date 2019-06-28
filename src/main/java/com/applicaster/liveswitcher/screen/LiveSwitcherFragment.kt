package com.applicaster.liveswitcher.screen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applicaster.atom.model.APAtomEntry
import com.applicaster.atom.model.APAtomError
import com.applicaster.atom.model.APAtomFeed
import com.applicaster.jspipes.JSManager
import com.applicaster.liveswitcher.R
import com.applicaster.liveswitcher.screen.adapter.ProgramAdapter
import com.applicaster.liveswitcher.utils.LiveSwitcherUtil
import com.applicaster.player.VideoAdsUtil
import com.applicaster.plugin_manager.playersmanager.AdsConfiguration
import com.applicaster.plugin_manager.playersmanager.Playable
import com.applicaster.plugin_manager.playersmanager.PlayableConfiguration
import com.applicaster.plugin_manager.playersmanager.internal.PlayersManager
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_live_switcher.*

class LiveSwitcherFragment : Fragment(), ProgramAdapter.OnProgramClickListener {

    var data: Any? = null

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
                            rv_live.layoutManager = LinearLayoutManager(context)
                            rv_live.adapter = ProgramAdapter(LiveSwitcherUtil
                                    .getLiveAtoms(atom.entries as ArrayList<APAtomEntry>),
                                    context, this@LiveSwitcherFragment, true)
                            ViewCompat.setNestedScrollingEnabled(rv_live, false)

                            rv_next.layoutManager = LinearLayoutManager(context)
                            rv_next.adapter = ProgramAdapter(LiveSwitcherUtil
                                    .getNextAtoms(atom.entries as ArrayList<APAtomEntry>),
                                    context, this@LiveSwitcherFragment, false)
                            ViewCompat.setNestedScrollingEnabled(rv_next, false)
                        }
                    }

                    override fun onError(error: APAtomError) {
                        Log.d(this.javaClass.simpleName, "onError")
                    }
                })
            }
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


}
