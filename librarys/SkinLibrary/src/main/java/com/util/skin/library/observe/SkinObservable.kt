package com.util.skin.library.observe

import java.util.*

open class SkinObservable {
    private val observers: ArrayList<SkinObserver> = ArrayList()

    @Synchronized
    fun addObserver(o: SkinObserver?) {
        if (o == null) {
            throw NullPointerException()
        }
        if (!observers.contains(o)) {
            observers.add(o)
        }
    }

    @Synchronized
    fun deleteObserver(o: SkinObserver) {
        observers.remove(o)
    }

    fun notifyUpdateSkin() {
        val arrLocal: Array<SkinObserver>

        synchronized(this) {
            arrLocal = observers.toTypedArray()
        }

        for (i in arrLocal.indices.reversed()) {
            arrLocal[i].updateSkin(this)
        }
    }

    @Synchronized
    fun deleteObservers() {
        observers.clear()
    }

    @Synchronized
    fun countObservers(): Int {
        return observers.size
    }
}
