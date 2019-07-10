package com.applicaster.liveswitcher.screen

import com.applicaster.atom.model.APAtomEntry

class LiveSwitcherPresenter(private var liveSwitcherView: LiveSwitcherView?,
                            private var liveSwitcherInteractor: LiveSwitcherInteractor)
    : LiveSwitcherInteractor.OnFinishedListener {

    fun getAtoms(data: Any?) {
        liveSwitcherView?.showProgress()
        liveSwitcherInteractor.getAtoms(data, this)
    }

    override fun onGetAtomsSuccess(atomEntries: List<APAtomEntry>, extensions: Map<String, Any>) {
        liveSwitcherView?.hideProgress()
        liveSwitcherView?.onAtomsFetchedSuccessfully(atomEntries, extensions)
    }

    override fun onGetAtomsFail() {
        liveSwitcherView?.hideProgress()
        liveSwitcherView?.onAtomsFetchedFail()
    }

}