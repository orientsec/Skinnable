package com.util.skinnable.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skinnable.support.design.R

/**
 * 支持换肤的 TabLayout 控件。
 * 能够自动捕获并替换背景色、指示器颜色 (tabIndicatorColor)、标签文本颜色 (tabTextColor) 及选中文本颜色 (tabSelectedTextColor)。
 */
@SuppressLint("CustomViewStyleable", "PrivateResource")
class SkinMaterialTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr), SkinSupportable {
    private var mTabIndicatorColorResId = INVALID_ID
    private var mTabTextColorsResId = INVALID_ID
    private var mTabSelectedTextColorResId = INVALID_ID
    private val mBackgroundHelper = SkinBackgroundHelper(this)

    override val skinnable: Boolean by lazy {
        return@lazy SkinResourcesManager.parseSkinnable(
            context,
            attrs,
            defStyleAttr
        )
    }

    init {
        mBackgroundHelper.loadFromAttributes(attrs, defStyleAttr)

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.TabLayout,
            defStyleAttr, 0
        )

        mTabIndicatorColorResId = a.getResourceId(R.styleable.TabLayout_tabIndicatorColor, INVALID_ID)

        val tabTextAppearance =
            a.getResourceId(R.styleable.TabLayout_tabTextAppearance, R.style.TextAppearance_Design_Tab)

        // Text colors/sizes come from the text appearance first
        val ta = context.obtainStyledAttributes(tabTextAppearance, R.styleable.SkinTextAppearance)
        try {
            mTabTextColorsResId = ta.getResourceId(R.styleable.SkinTextAppearance_android_textColor, INVALID_ID)
        } finally {
            ta.recycle()
        }

        if (a.hasValue(R.styleable.TabLayout_tabTextColor)) {
            // If we have an explicit text color set, use it instead
            mTabTextColorsResId = a.getResourceId(R.styleable.TabLayout_tabTextColor, INVALID_ID)
        }

        if (a.hasValue(R.styleable.TabLayout_tabSelectedTextColor)) {
            // We have an explicit selected text color set, so we need to make merge it with the
            // current colors.
            mTabSelectedTextColorResId = a.getResourceId(R.styleable.TabLayout_tabSelectedTextColor, INVALID_ID)
        }
        a.recycle()
        applySkin()
    }

    override fun applySkin() {
        mBackgroundHelper.applySkin()

        if (SkinHelper.checkResourceIdValid(mTabIndicatorColorResId)) {
            setSelectedTabIndicatorColor(SkinResourcesManager.getColor(context, mTabIndicatorColorResId))
        }
        if (SkinHelper.checkResourceIdValid(mTabTextColorsResId)) {
            tabTextColors = SkinResourcesManager.getColorStateList(context, mTabTextColorsResId)
        }
        if (SkinHelper.checkResourceIdValid(mTabSelectedTextColorResId)) {
            val selected = SkinResourcesManager.getColor(context, mTabSelectedTextColorResId)
            tabTextColors?.let {
                setTabTextColors(it.defaultColor, selected)
            }
        }
    }
}
