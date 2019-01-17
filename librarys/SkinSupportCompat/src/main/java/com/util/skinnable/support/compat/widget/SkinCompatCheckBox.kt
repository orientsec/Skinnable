package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatCheckBox
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.R
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skinnable.support.compat.helpers.SkinCompoundButtonHelper
import com.util.skinnable.support.compat.helpers.SkinTextHelper

class SkinCompatCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.checkboxStyle
) : AppCompatCheckBox(context, attrs, defStyleAttr), SkinSupportable {
    private val mCompoundButtonHelper = SkinCompoundButtonHelper(this)
    private val mTextHelper = SkinTextHelper.create(this)
    private val mBackgroundTintHelper: SkinBackgroundHelper =
        SkinBackgroundHelper(this)
    override val skinnable: Boolean by lazy {
        return@lazy SkinResourcesManager.parseSkinnable(
            context,
            attrs,
            defStyleAttr
        )
    }

    init {
        mCompoundButtonHelper.loadFromAttributes(attrs, defStyleAttr)
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        mTextHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setButtonDrawable(@DrawableRes resId: Int) {
        super.setButtonDrawable(resId)
        mCompoundButtonHelper.setSrcId(resId)
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)
        mBackgroundTintHelper.setSrcId(resId)
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
