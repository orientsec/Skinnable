package com.util.skin.library.helpers

import android.util.AttributeSet
import android.view.View
import com.util.skin.library.R

abstract class SkinHelper(protected open val mView: View) {

    protected var mSrcId: Int = INVALID_ID

    var skinnable = false

    /**
     * 刷新UI
     */
    abstract fun applySkin()

    /**
     * 加载资源布局
     */
    open fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = mView.context.obtainStyledAttributes(attrs, R.styleable.SkinSupportable, defStyleAttr, 0)
        try {
            if (a.hasValue(R.styleable.SkinSupportable_skinnable)) {
                skinnable = a.getBoolean(R.styleable.SkinSupportable_skinnable, false)
            }
        } finally {
            a.recycle()
        }
    }

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
