package com.applicaster.liveswitcher

import android.content.Context
import android.support.v4.app.Fragment
import android.util.Log
import com.applicaster.liveswitcher.screen.LiveSwitcherFragment
import com.applicaster.plugin_manager.screen.PluginScreen
import java.io.Serializable
import java.util.HashMap

class LiveSwitcherContract : PluginScreen {
    override fun present(context: Context?, screenMap: HashMap<String, Any>?, dataSource: Serializable?, isActivity: Boolean) {
        // todo: implement this
        Log.d("LiveSwitcherContract", "present")
    }

    override fun generateFragment(screenMap: HashMap<String, Any>?, dataSource: Serializable?): Fragment {
        val fragment = LiveSwitcherFragment()
        fragment.data = screenMap?.get("data")
        return fragment
    }

}