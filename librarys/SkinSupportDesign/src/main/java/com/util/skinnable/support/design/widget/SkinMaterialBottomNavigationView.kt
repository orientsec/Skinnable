package com.util.skinnable.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.widget.SkinSupportable
import com.util.skin.library.res.SkinResourcesManager
import com.util.skinnable.support.design.R

@SuppressLint("CustomViewStyleable", "PrivateResource")
class SkinMaterialBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr), SkinSupportable {

    private var mTextColorResId = INVALID_ID
    private var mIconTintResId = INVALID_ID
    private var mDefaultTintResId = INVALID_ID
    override val skinnable: Boolean

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.BottomNavigationView, defStyleAttr,
            R.style.Widget_Design_BottomNavigationView
        )

        if (a.hasValue(R.styleable.BottomNavigationView_itemIconTint)) {
            mIconTintResId = a.getResourceId(R.styleable.BottomNavigationView_itemIconTint, INVALID_ID)
        } else {
            mDefaultTintResId = resolveColorPrimary()
        }
        if (a.hasValue(R.styleable.BottomNavigationView_itemTextColor)) {
            mTextColorResId = a.getResourceId(R.styleable.BottomNavigationView_itemTextColor, INVALID_ID)
        } else {
            mDefaultTintResId = resolveColorPrimary()
        }
        skinnable = a.getBoolean(R.styleable.SkinSupportable_skinnable, false)
        a.recycle()
        applyItemIconTintResource()
        applyItemTextColorResource()
    }

    private fun applyItemTextColorResource() {
        if (SkinHelper.checkResourceIdValid(mTextColorResId)) {
            itemTextColor = SkinResourcesManager.getColorStateList(context, mTextColorResId)
        } else {
            if (SkinHelper.checkResourceIdValid(mDefaultTintResId)) {
                itemTextColor = createDefaultColorStateList(android.R.attr.textColorSecondary)
            }
        }
    }

    private fun applyItemIconTintResource() {
        if (SkinHelper.checkResourceIdValid(mIconTintResId)) {
            itemIconTintList = SkinResourcesManager.getColorStateList(context, mIconTintResId)
        } else {
            if (SkinHelper.checkResourceIdValid(mDefaultTintResId)) {
                itemIconTintList = createDefaultColorStateList(android.R.attr.textColorSecondary)
            }
        }
    }

    private fun createDefaultColorStateList(baseColorThemeAttr: Int): ColorStateList? {
        val value = TypedValue()
        if (!context.theme.resolveAttribute(baseColorThemeAttr, value, true)) {
            return null
        }
        val baseColor = SkinResourcesManager.getColorStateList(context, value.resourceId)

        val colorPrimary = SkinResourcesManager.getColor(context, mDefaultTintResId)
        val defaultColor = baseColor?.defaultColor ?: colorPrimary
        return ColorStateList(
            arrayOf(DISABLED_STATE_SET, CHECKED_STATE_SET, EMPTY_STATE_SET),
            intArrayOf(
                baseColor?.getColorForState(DISABLED_STATE_SET, defaultColor) ?: colorPrimary,
                colorPrimary,
                defaultColor
            )
        )
    }

    private fun resolveColorPrimary(): Int {
        val value = TypedValue()
        return if (!context.theme.resolveAttribute(
                R.attr.colorPrimary, value, true
            )
        ) {
            INVALID_ID
        } else value.resourceId
    }

    override fun applySkin() {
        applyItemIconTintResource()
        applyItemTextColorResource()
    }

    companion object {

        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
        private val DISABLED_STATE_SET = intArrayOf(-android.R.attr.state_enabled)
    }

}
