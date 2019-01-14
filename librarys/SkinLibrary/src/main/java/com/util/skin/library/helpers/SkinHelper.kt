package com.util.skin.library.helpers

abstract class SkinHelper {

    abstract fun applySkin()

    companion object {
        private const val SYSTEM_ID_PREFIX = "1"
        const val INVALID_ID = 0

        fun checkResourceIdValid(resId: Int): Boolean {
            val hexResId = Integer.toHexString(resId)
            return hexResId.startsWith(SYSTEM_ID_PREFIX)
        }
    }
}
