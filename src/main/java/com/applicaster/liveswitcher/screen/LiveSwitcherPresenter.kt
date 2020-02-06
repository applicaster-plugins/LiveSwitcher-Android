package com.applicaster.liveswitcher.screen

import rx.Subscription

class LiveSwitcherPresenter(private var liveSwitcherView: LiveSwitcherView?,
                            private var liveSwitcherInteractor: LiveSwitcherInteractor) {

    var subscription: Subscription? = null

    fun getAtoms(data: Any?) {
        liveSwitcherView?.showProgress()
        subscription = liveSwitcherInteractor.getAtoms(data)
                .subscribe({
                    liveSwitcherView?.hideProgress()
                    liveSwitcherView?.onAtomsFetchedSuccessfully(it.entries, it.extensions)
                }, {
                    liveSwitcherView?.hideProgress()
                    liveSwitcherView?.onAtomsFetchedFail()
                })
    }

    fun dispose() {
        subscription?.unsubscribe()
    }
}