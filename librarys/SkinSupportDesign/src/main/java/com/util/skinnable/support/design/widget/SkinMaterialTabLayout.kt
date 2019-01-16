package com.util.skinnable.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.design.R

@SuppressLint("CustomViewStyleable", "PrivateResource")
class SkinMaterialTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr), SkinSupportable {
    private var mTabIndicatorColorResId = INVALID_ID
    private var mTabTextColorsResId = INVALID_ID
    private var mTabSelectedTextColorResId = INVALID_ID
    override val skinnable: Boolean

    init {
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
            // current colors. This is exposed so that developers can use theme attributes to set
            // this (theme attrs in ColorStateLists are Lollipop+)
            mTabSelectedTextColorResId = a.getResourceId(R.styleable.TabLayout_tabSelectedTextColor, INVALID_ID)
        }
        skinnable = a.getBoolean(R.styleable.SkinSupportable_skinnable, false)
        a.recycle()
        applySkin()
    }

    override fun applySkin() {
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
