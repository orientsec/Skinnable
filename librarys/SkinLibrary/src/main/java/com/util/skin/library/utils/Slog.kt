package com.util.skin.library.utils

import android.util.Log

interface SkinLogger {
    fun log(level: Int, tag: String, msg: String, t: Throwable? = null)
}

object Slog {
    var DEBUG = false
    var logger: SkinLogger? = null
    private const val TAG = "skin-support"

    fun i(msg: String) {
        if (DEBUG) {
            logger?.log(Log.INFO, TAG, msg) ?: Log.i(TAG, msg)
        }
    }

    fun i(subtag: String, msg: String) {
        if (DEBUG) {
            logger?.log(Log.INFO, TAG, "$subtag: $msg") ?: Log.i(TAG, "$subtag: $msg")
        }
    }

    fun r(msg: String) {
        logger?.log(Log.INFO, TAG, msg) ?: Log.i(TAG, msg)
    }

    fun r(subtag: String, msg: String) {
        logger?.log(Log.INFO, TAG, "$subtag: $msg") ?: Log.i(TAG, "$subtag: $msg")
    }

    fun e(subtag: String, msg: String, t: Throwable? = null) {
        logger?.log(Log.ERROR, TAG, "$subtag: $msg", t) ?: Log.e(TAG, "$subtag: $msg", t)
    }
}
