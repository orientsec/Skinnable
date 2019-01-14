package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatCheckBox
import com.util.skin.library.R
import com.util.skinnable.support.compat.helpers.SkinCompatBackgroundHelper
import com.util.skinnable.support.compat.helpers.SkinCompatTextHelper
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinCompatCompoundButtonHelper


class SkinCompatCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.checkboxStyle
) : AppCompatCheckBox(context, attrs, defStyleAttr), SkinSupportable {
    private val mCompoundButtonHelper= SkinCompatCompoundButtonHelper(this)
    private val mTextHelper = SkinCompatTextHelper.create(this)
    private val mBackgroundTintHelper: SkinCompatBackgroundHelper =
        SkinCompatBackgroundHelper(this)

    init {
        mCompoundButtonHelper.loadFromAttributes(attrs, defStyleAttr)
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        mTextHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setButtonDrawable(@DrawableRes resId: Int) {
        super.setButtonDrawable(resId)
        mCompoundButtonHelper.setButtonDrawable(resId)
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)
        mBackgroundTintHelper.onSetBackgroundResource(resId)
    }

    override fun setTextAppearance(resId: Int) {
        setTextAppearance(context, resId)
    }

    override fun setTextAppearance(context: Context, resId: Int) {
        super.setTextAppearance(context, resId)
        mTextHelper.onSetTextAppearance(context, resId)
    }

    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(
        @DrawableRes start: Int, @DrawableRes top: Int, @DrawableRes end: Int, @DrawableRes bottom: Int
    ) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
        mTextHelper.onSetCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(
        @DrawableRes left: Int, @DrawableRes top: Int, @DrawableRes right: Int, @DrawableRes bottom: Int
    ) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
        mTextHelper.onSetCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
    }

    override fun applySkin() {
        mCompoundButtonHelper.applySkin()
        mBackgroundTintHelper.applySkin()
        mTextHelper.applySkin()
    }

}
