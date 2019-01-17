package com.util.skinnable.support.compat.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.R
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skinnable.support.compat.helpers.SkinTextHelper
import com.util.skinnable.support.compat.res.SkinCompatVectorResources

class SkinCompatCheckedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.checkedTextViewStyle
) : AppCompatCheckedTextView(context, attrs, defStyleAttr), SkinSupportable {
    private var mCheckMarkResId = INVALID_ID

    private val mTextHelper = SkinTextHelper.create(this)
    private val mBackgroundTintHelper = SkinBackgroundHelper(this)
    override val skinnable: Boolean by lazy {
        return@lazy SkinResourcesManager.parseSkinnable(
            context,
            attrs,
            defStyleAttr
        )
    }

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        mTextHelper.loadFromAttributes(attrs, defStyleAttr)

        val a = context.obtainStyledAttributes(attrs, TINT_ATTRS, defStyleAttr, 0)
        mCheckMarkResId = a.getResourceId(0, INVALID_ID)
        a.recycle()
        applyCheckMark()
    }

    override fun setCheckMarkDrawable(@DrawableRes resId: Int) {
        mCheckMarkResId = resId
        applyCheckMark()
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
        mBackgroundTintHelper.applySkin()
        mTextHelper.applySkin()
        applyCheckMark()
    }

    private fun applyCheckMark() {
        if (SkinHelper.checkResourceIdValid(mCheckMarkResId)) {
            checkMarkDrawable = SkinCompatVectorResources.getDrawableCompat(context, mCheckMarkResId)
        }
    }

    companion object {

        private val TINT_ATTRS = intArrayOf(android.R.attr.checkMark)
    }
}
