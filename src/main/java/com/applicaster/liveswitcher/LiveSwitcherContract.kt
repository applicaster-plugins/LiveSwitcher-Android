package com.applicaster.liveswitcher

import android.content.Context
import android.support.v4.app.Fragment
import android.util.Log
import com.applicaster.liveswitcher.screen.LiveSwitcherFragment
import com.applicaster.plugin_manager.screen.PluginScreen
import com.google.gson.internal.LinkedTreeMap
import java.io.Serializable
import java.util.HashMap

class LiveSwitcherContract : PluginScreen {
    companion object {
        var configuration: LinkedTreeMap<*, *>? = null
    }

    override fun present(context: Context?, screenMap: HashMap<String, Any>?, dataSource: Serializable?, isActivity: Boolean) {
        // todo: implement this
        Log.d("LiveSwitcherContract", "present")
    }

    override fun generateFragment(screenMap: HashMap<String, Any>?, dataSource: Serializable?): Fragment {
        val fragment = LiveSwitcherFragment()
        fragment.data = screenMap?.get("data")
        configuration = screenMap?.get("general") as LinkedTreeMap<*, *>
        return fragment
    }
}