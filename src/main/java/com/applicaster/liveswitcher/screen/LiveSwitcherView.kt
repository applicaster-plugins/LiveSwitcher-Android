package com.applicaster.liveswitcher.screen

import com.applicaster.atom.model.APAtomEntry

interface LiveSwitcherView {
    fun showProgress()
    fun hideProgress()
    fun onAtomsFetchedSuccessfully(atomEntries: List<APAtomEntry>, extensions: Map<String, Any>)
    fun onAtomsFetchedFail()
}