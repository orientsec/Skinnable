package com.util.skinnable.support.compat.helpers

import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ImageView
import com.util.skin.library.R
import com.util.skin.library.helpers.SkinHelper
import com.util.skinnable.support.compat.res.SkinCompatVectorResources

class SkinCompatImageHelper(private val mView: ImageView) : SkinHelper() {
    private var mSrcResId = INVALID_ID
    private var mSrcCompatResId = INVALID_ID

    fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        var a: TypedArray? = null
        try {
            a = mView.context.obtainStyledAttributes(attrs, R.styleable.SkinCompatImageView, defStyleAttr, 0)
            mSrcResId = a!!.getResourceId(R.styleable.SkinCompatImageView_android_src, INVALID_ID)
            mSrcCompatResId = a.getResourceId(R.styleable.SkinCompatImageView_srcCompat, INVALID_ID)
        } finally {
            a?.recycle()
        }
        applySkin()
    }

    fun setImageResource(resId: Int) {
        mSrcResId = resId
        applySkin()
    }

    override fun applySkin() {
        if (checkResourceIdValid(mSrcCompatResId)) {
            val drawable = SkinCompatVectorResources.getDrawableCompat(mView.context, mSrcCompatResId)
            if (drawable != null) {
                mView.setImageDrawable(drawable)
            }
        } else {
            if (checkResourceIdValid(mSrcResId)) {
                val drawable = SkinCompatVectorResources.getDrawableCompat(mView.context, mSrcResId)
                if (drawable != null) {
                    mView.setImageDrawable(drawable)
                }
            }
        }
    }
}
