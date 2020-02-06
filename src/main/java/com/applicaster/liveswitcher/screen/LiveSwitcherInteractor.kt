package com.applicaster.liveswitcher.screen

import android.util.Log
import com.applicaster.atom.model.APAtomFeed
import com.applicaster.jspipes.JSManager
import com.google.gson.internal.LinkedTreeMap
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LiveSwitcherInteractor {

    fun getAtoms(data: Any?): Observable<APAtomFeed> {
        if (data is LinkedTreeMap<*, *>) {
            val source = data["source"].toString()
                return JSManager.getInstance().get(source)
                        .map { it as APAtomFeed }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnCompleted { Log.d(this.javaClass.simpleName, "onResult") }
                        .doOnError { Log.d(this.javaClass.simpleName, "onError") }
        }
        return Observable.empty()
    }
}