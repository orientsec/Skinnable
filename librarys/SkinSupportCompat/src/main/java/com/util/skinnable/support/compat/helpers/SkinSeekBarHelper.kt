package com.util.skinnable.support.compat.helpers

import android.annotation.SuppressLint
import android.util.AttributeSet
import android.widget.SeekBar
import com.util.skinnable.support.compat.R
import com.util.skinnable.support.compat.res.SkinCompatVectorResources

@SuppressLint("PrivateResource")
class SkinSeekBarHelper(private val mView: SeekBar) : SkinProgressBarHelper(mView) {

    private var mThumbResId = INVALID_ID

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        super.loadFromAttributes(attrs, defStyleAttr)

        val a = mView.context.obtainStyledAttributes(attrs, R.styleable.AppCompatSeekBar, defStyleAttr, 0)
        mThumbResId = a.getResourceId(
            R.styleable.AppCompatSeekBar_android_thumb,
            INVALID_ID
        )
        a.recycle()

        applySkin()
    }

    override fun applySkin() {
        super.applySkin()
        if (checkResourceIdValid(mThumbResId)) {
            mView.thumb = SkinCompatVectorResources.getDrawableCompat(mView.context, mThumbResId)
        }
    }
}
