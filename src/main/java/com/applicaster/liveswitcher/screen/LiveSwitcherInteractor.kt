package com.applicaster.liveswitcher.screen

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.applicaster.atom.model.APAtomEntry
import com.applicaster.atom.model.APAtomError
import com.applicaster.atom.model.APAtomFeed
import com.applicaster.jspipes.JSManager
import com.google.gson.internal.LinkedTreeMap

class LiveSwitcherInteractor {

    interface OnFinishedListener {
        fun onGetAtomsSuccess(atomEntries: List<APAtomEntry>, extensions: Map<String, Any>)
        fun onGetAtomsFail()
    }

    fun getAtoms(data: Any?, onFinishedListener: OnFinishedListener) {
        if (data is LinkedTreeMap<*, *>) {
            val source = data["source"].toString()
            Handler(Looper.getMainLooper()).post {
                JSManager.getInstance().get(source, object : JSManager.JSManagerCallback {
                    override fun onResult(atom: Any) {
                        Log.d(this.javaClass.simpleName, "onResult")
                        if (atom is APAtomFeed) {
                            onFinishedListener.onGetAtomsSuccess(atom.entries, atom.extensions)
                        }
                    }

                    override fun onError(error: APAtomError) {
                        Log.d(this.javaClass.simpleName, "onError")
                        onFinishedListener.onGetAtomsFail()
                    }
                })
            }
        }
    }
}