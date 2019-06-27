package com.applicaster.liveswitcher.screen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
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
import com.applicaster.util.ui.Toaster
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
                            rv_programs.layoutManager = LinearLayoutManager(context)
                            rv_programs.adapter = ProgramAdapter(LiveSwitcherUtil
                                    .getProgramsFromAtomEntries(atom.entries as ArrayList<APAtomEntry>),
                                    context, this@LiveSwitcherFragment)
                        }
                    }

                    override fun onError(error: APAtomError) {
                        Log.d(this.javaClass.simpleName, "onError")
                    }
                })
            }
        }
    }

    override fun onProgramClicked(programId: String) {
        Toaster.makeToast(context, "the click got through")
    }
}
