package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import com.util.skinnable.support.compat.helpers.SkinCompatBackgroundHelper
import com.util.skinnable.support.compat.helpers.SkinCompatImageHelper
import com.util.skin.library.widget.SkinSupportable

class SkinCompatImageButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.imageButtonStyle
) : AppCompatImageButton(context, attrs, defStyleAttr), SkinSupportable {
    private val mBackgroundTintHelper= SkinCompatBackgroundHelper(this)
    private val mImageHelper = SkinCompatImageHelper(this)

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        mImageHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)
        mBackgroundTintHelper.onSetBackgroundResource(resId)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        // Intercept this call and instead retrieve the Drawable via the image helper
        mImageHelper.setImageResource(resId)
    }

    override fun applySkin() {
        mBackgroundTintHelper.applySkin()
        mImageHelper.applySkin()
    }

}
