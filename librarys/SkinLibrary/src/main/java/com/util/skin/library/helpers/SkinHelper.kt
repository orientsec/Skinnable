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
        const val INVALID_ID = 0

        fun checkResourceIdValid(resId: Int): Boolean {
            return resId != INVALID_ID
        }
    }
}
