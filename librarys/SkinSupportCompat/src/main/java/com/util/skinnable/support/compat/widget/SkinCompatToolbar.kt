package com.util.skinnable.support.compat.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.R
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skinnable.support.compat.res.SkinCompatVectorResources

@SuppressLint("CustomViewStyleable", "PrivateResource")
class SkinCompatToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.toolbarStyle
) : Toolbar(context, attrs, defStyleAttr), SkinSupportable {
    private var mTitleTextColorResId = INVALID_ID
    private var mSubtitleTextColorResId = INVALID_ID
    private var mNavigationIconResId = INVALID_ID
    private val mBackgroundTintHelper: SkinBackgroundHelper?

    init {
        mBackgroundTintHelper = SkinBackgroundHelper(this)
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)

        var a = context.obtainStyledAttributes(attrs, R.styleable.Toolbar, defStyleAttr, 0)
        mNavigationIconResId = a.getResourceId(R.styleable.Toolbar_navigationIcon, INVALID_ID)

        val titleAp = a.getResourceId(R.styleable.Toolbar_titleTextAppearance, INVALID_ID)
        val subtitleAp = a.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, INVALID_ID)
        a.recycle()
        if (titleAp != INVALID_ID) {
            a = context.obtainStyledAttributes(titleAp, R.styleable.SkinTextAppearance)
            mTitleTextColorResId = a.getResourceId(R.styleable.SkinTextAppearance_android_textColor, INVALID_ID)
            a.recycle()
        }
        if (subtitleAp != INVALID_ID) {
            a = context.obtainStyledAttributes(subtitleAp, R.styleable.SkinTextAppearance)
            mSubtitleTextColorResId = a.getResourceId(R.styleable.SkinTextAppearance_android_textColor, INVALID_ID)
            a.recycle()
        }
        a = context.obtainStyledAttributes(attrs, R.styleable.Toolbar, defStyleAttr, 0)
        if (a.hasValue(R.styleable.Toolbar_titleTextColor)) {
            mTitleTextColorResId = a.getResourceId(R.styleable.Toolbar_titleTextColor, INVALID_ID)
        }
        if (a.hasValue(R.styleable.Toolbar_subtitleTextColor)) {
            mSubtitleTextColorResId = a.getResourceId(R.styleable.Toolbar_subtitleTextColor, INVALID_ID)
        }
        a.recycle()
        applyTitleTextColor()
        applySubtitleTextColor()
        applyNavigationIcon()
    }

    private fun applyTitleTextColor() {
        if (SkinHelper.checkResourceIdValid(mTitleTextColorResId)) {
            setTitleTextColor(SkinResourcesManager.getColor(context, mTitleTextColorResId))
        }
    }

    private fun applySubtitleTextColor() {
        if (SkinHelper.checkResourceIdValid(mSubtitleTextColorResId)) {
            setSubtitleTextColor(SkinResourcesManager.getColor(context, mSubtitleTextColorResId))
        }
    }

    private fun applyNavigationIcon() {
        if (SkinHelper.checkResourceIdValid(mNavigationIconResId)) {
            navigationIcon = SkinCompatVectorResources.getDrawableCompat(context, mNavigationIconResId)
        }
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)
        mBackgroundTintHelper?.setSrcId(resId)
    }

    override fun setNavigationIcon(@DrawableRes resId: Int) {
        super.setNavigationIcon(resId)
        mNavigationIconResId = resId
        applyNavigationIcon()
    }

    override fun applySkin() {
        mBackgroundTintHelper?.applySkin()
        applyTitleTextColor()
        applySubtitleTextColor()
        applyNavigationIcon()
    }

}
