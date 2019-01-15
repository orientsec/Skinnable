package com.util.skin.library.helpers

import android.util.AttributeSet

abstract class SkinHelper {

    protected var mSrcId: Int = INVALID_ID

    /**
     * 刷新UI
     */
    abstract fun applySkin()

    /**
     * 加载资源布局
     */
    abstract fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int)

    open fun setSrcId(srcId: Int) {
        mSrcId = srcId
        applySkin()
    }

    companion object {
        private const val SYSTEM_ID_PREFIX = "1"
        const val INVALID_ID = 0

        fun checkResourceIdValid(resId: Int): Boolean {
            val hexResId = Integer.toHexString(resId)
            return hexResId.startsWith(SYSTEM_ID_PREFIX)
        }
    }
}
