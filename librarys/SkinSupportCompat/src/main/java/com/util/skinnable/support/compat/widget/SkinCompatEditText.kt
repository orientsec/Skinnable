package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinCompatBackgroundHelper
import com.util.skinnable.support.compat.helpers.SkinCompatTextHelper

class SkinCompatEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr), SkinSupportable {
    private val mTextHelper = SkinCompatTextHelper.create(this)
    private val mBackgroundTintHelper = SkinCompatBackgroundHelper(this)

    val textColorResId: Int
        get() = mTextHelper.textColorResId

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        mTextHelper.loadFromAttributes(attrs, defStyleAttr)
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
        mBackgroundTintHelper.applySkin()
        mTextHelper.applySkin()
    }

}
