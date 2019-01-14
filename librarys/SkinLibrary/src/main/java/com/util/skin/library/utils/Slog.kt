package com.util.skin.library.utils

import android.util.Log

object Slog {
    var DEBUG = false
    private const val TAG = "skin-support"

    fun i(msg: String) {
        if (DEBUG) {
            Log.i(TAG, msg)
        }
    }

    fun i(subtag: String, msg: String) {
        if (DEBUG) {
            Log.i(TAG, "$subtag: $msg")
        }
    }

    fun r(msg: String) {
        Log.i(TAG, msg)
    }

    fun r(subtag: String, msg: String) {
        Log.i(TAG, "$subtag: $msg")
    }
}
