package com.applicaster.liveswitcher.screen

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.applicaster.atom.model.APAtomEntry
import com.applicaster.atom.model.APAtomError
import com.applicaster.atom.model.APAtomFeed
import com.applicaster.jspipes.JSManager
import com.google.gson.internal.LinkedTreeMap
import rx.android.schedulers.AndroidSchedulers

class LiveSwitcherInteractor {

    interface OnFinishedListener {
        fun onGetAtomsSuccess(atomEntries: List<APAtomEntry>, extensions: Map<String, Any>)
        fun onGetAtomsFail()
    }

    fun getAtoms(data: Any?, onFinishedListener: OnFinishedListener) {
        if (data is LinkedTreeMap<*, *>) {
            val source = data["source"].toString()
            JSManager
                .getInstance()
                .get(source)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    (it as? APAtomFeed?)?.let { atom ->
                        Log.d(this.javaClass.simpleName, "onResult")
                        onFinishedListener.onGetAtomsSuccess(atom.entries, atom.extensions)
                    }
                }, {
                    Log.d(this.javaClass.simpleName, "onError")
                    onFinishedListener.onGetAtomsFail()
                })
        }
    }
}
