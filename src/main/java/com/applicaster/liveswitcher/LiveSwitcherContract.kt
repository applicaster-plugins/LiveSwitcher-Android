package com.applicaster.liveswitcher

import android.content.Context
import android.support.v4.app.Fragment
import com.applicaster.plugin_manager.screen.PluginScreen
import java.io.Serializable
import java.util.HashMap

class LiveSwitcherContract : PluginScreen {
    override fun present(context: Context?, screenMap: HashMap<String, Any>?, dataSource: Serializable?, isActivity: Boolean) {
        // todo: implement this
    }

    override fun generateFragment(screenMap: HashMap<String, Any>?, dataSource: Serializable?): Fragment {
        return Fragment()
    }

}