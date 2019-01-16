package com.util.skin.library.observe

open class SkinObservable {
    private val observers: HashSet<SkinObserver> = hashSetOf()

    @Synchronized
    fun addObserver(o: SkinObserver?) {
        if (o == null) {
            throw NullPointerException()
        }
        observers.add(o)
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
