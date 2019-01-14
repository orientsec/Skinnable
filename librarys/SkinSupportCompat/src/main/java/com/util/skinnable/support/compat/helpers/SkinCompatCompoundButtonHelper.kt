package com.util.skinnable.support.compat.helpers

import android.annotation.SuppressLint
import android.util.AttributeSet
import android.widget.CompoundButton
import androidx.core.widget.CompoundButtonCompat
import com.util.skin.library.R
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.res.SkinResourcesManager
import com.util.skinnable.support.compat.res.SkinCompatVectorResources

@SuppressLint("PrivateResource")
class SkinCompatCompoundButtonHelper(private val mView: CompoundButton) : SkinHelper() {
    private var mButtonResourceId = INVALID_ID
    private var mButtonTintResId = INVALID_ID


    internal fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = mView.context.obtainStyledAttributes(
            attrs, R.styleable.CompoundButton,
            defStyleAttr, INVALID_ID
        )
        try {
            if (a.hasValue(R.styleable.CompoundButton_android_button)) {
                mButtonResourceId = a.getResourceId(
                    R.styleable.CompoundButton_android_button,
                    INVALID_ID
                )
            }

            if (a.hasValue(R.styleable.CompoundButton_buttonTint)) {
                mButtonTintResId = a.getResourceId(
                    R.styleable.CompoundButton_buttonTint,
                    INVALID_ID
                )
            }
        } finally {
            a.recycle()
        }
        applySkin()
    }

    fun setButtonDrawable(resId: Int) {
        mButtonResourceId = resId
        applySkin()
    }

    override fun applySkin() {
        if (checkResourceIdValid(mButtonResourceId)) {
            mView.buttonDrawable = SkinCompatVectorResources.getDrawableCompat(mView.context, mButtonResourceId)
        }
        if (checkResourceIdValid(mButtonTintResId)) {
            CompoundButtonCompat.setButtonTintList(
                mView,
                SkinResourcesManager.getColorStateList(mView.context, mButtonTintResId)
            )
        }
    }
}
