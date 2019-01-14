package com.util.skinnable.support.compat.helpers

import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import com.util.skin.library.R
import com.util.skin.library.helpers.SkinHelper
import com.util.skinnable.support.compat.res.SkinCompatVectorResources

class SkinCompatBackgroundHelper(private val mView: View) : SkinHelper() {

    private var mBackgroundResId = INVALID_ID

    fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = mView.context.obtainStyledAttributes(attrs, R.styleable.SkinBackgroundHelper, defStyleAttr, 0)
        try {
            if (a.hasValue(R.styleable.SkinBackgroundHelper_android_background)) {
                mBackgroundResId = a.getResourceId(
                    R.styleable.SkinBackgroundHelper_android_background, INVALID_ID
                )
            }
        } finally {
            a.recycle()
        }
        applySkin()
    }

    fun onSetBackgroundResource(resId: Int) {
        mBackgroundResId = resId
        // Update the default background tint
        applySkin()
    }

    override fun applySkin() {
        if (!checkResourceIdValid(mBackgroundResId)) {
            return
        }
        val drawable = SkinCompatVectorResources.getDrawableCompat(mView.context, mBackgroundResId)
        if (drawable != null) {
            val paddingLeft = mView.paddingLeft
            val paddingTop = mView.paddingTop
            val paddingRight = mView.paddingRight
            val paddingBottom = mView.paddingBottom
            ViewCompat.setBackground(mView, drawable)
            mView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        }
    }
}
