package com.util.skinnable.support.compat.helpers

import android.annotation.SuppressLint
import android.util.AttributeSet
import android.view.View
import android.widget.CompoundButton
import androidx.core.widget.CompoundButtonCompat
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.res.SkinResourcesManager
import com.util.skinnable.support.compat.R
import com.util.skinnable.support.compat.res.SkinCompatVectorResources

@SuppressLint("PrivateResource")
class SkinCompoundButtonHelper(view: CompoundButton) : SkinHelper(view) {
    private var mButtonTintResId = INVALID_ID
    override val mView: CompoundButton
        get() = super.mView as CompoundButton

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        super.loadFromAttributes(attrs, defStyleAttr)
        val a = mView.context.obtainStyledAttributes(
            attrs, R.styleable.CompoundButton,
            defStyleAttr, INVALID_ID
        )
        try {
            if (a.hasValue(R.styleable.CompoundButton_android_button)) {
                mSrcId = a.getResourceId(R.styleable.CompoundButton_android_button, INVALID_ID)
            }
            if (a.hasValue(R.styleable.CompoundButton_buttonTint)) {
                mButtonTintResId = a.getResourceId(R.styleable.CompoundButton_buttonTint, INVALID_ID)
            }
        } finally {
            a.recycle()
        }
        applySkin()
    }

    override fun applySkin() {
        if (checkResourceIdValid(mSrcId)) {
            mView.buttonDrawable = SkinCompatVectorResources.getDrawableCompat(
                mView.context,
                mSrcId
            )
        }
        if (checkResourceIdValid(mButtonTintResId)) {
            CompoundButtonCompat.setButtonTintList(
                mView,
                SkinResourcesManager.getColorStateList(mView.context, mButtonTintResId)
            )
        }
    }
}
