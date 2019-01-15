package com.util.skinnable.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import com.google.android.material.navigation.NavigationView
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.res.SkinThemeUtils
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.util.SkinCompatV7ThemeUtils
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skinnable.support.compat.res.SkinCompatVectorResources
import com.util.skin.library.res.SkinResourcesManager
import com.util.skinnable.support.design.R

@SuppressLint("CustomViewStyleable", "PrivateResource")
class SkinMaterialNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NavigationView(context, attrs, defStyleAttr), SkinSupportable {
    private var mItemBackgroundResId = INVALID_ID
    private var mTextColorResId = INVALID_ID
    private var mDefaultTintResId = INVALID_ID
    private var mIconTintResId = INVALID_ID
    private val mBackgroundTintHelper = SkinBackgroundHelper(this)

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, 0)

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.NavigationView, defStyleAttr,
            R.style.Widget_Design_NavigationView
        )
        if (a.hasValue(R.styleable.NavigationView_itemIconTint)) {
            mIconTintResId = a.getResourceId(R.styleable.NavigationView_itemIconTint, INVALID_ID)
        } else {
            mDefaultTintResId = SkinCompatV7ThemeUtils.getColorPrimaryResId(context)
        }
        if (a.hasValue(R.styleable.NavigationView_itemTextAppearance)) {
            val textAppearance = a.getResourceId(R.styleable.NavigationView_itemTextAppearance, INVALID_ID)
            if (textAppearance != INVALID_ID) {
                val ap = context.obtainStyledAttributes(textAppearance, R.styleable.SkinTextAppearance)
                if (ap.hasValue(R.styleable.SkinTextAppearance_android_textColor)) {
                    mTextColorResId = ap.getResourceId(R.styleable.SkinTextAppearance_android_textColor, INVALID_ID)
                }
                ap.recycle()
            }
        }
        if (a.hasValue(R.styleable.NavigationView_itemTextColor)) {
            mTextColorResId = a.getResourceId(R.styleable.NavigationView_itemTextColor, INVALID_ID)
        } else {
            mDefaultTintResId = SkinCompatV7ThemeUtils.getColorPrimaryResId(context)
        }
        if (mTextColorResId == INVALID_ID) {
            mTextColorResId = SkinThemeUtils.getTextColorPrimaryResId(context)
        }
        mItemBackgroundResId = a.getResourceId(R.styleable.NavigationView_itemBackground, INVALID_ID)
        a.recycle()
        applyItemIconTintResource()
        applyItemTextColorResource()
        applyItemBackgroundResource()
    }

    override fun setItemBackgroundResource(@DrawableRes resId: Int) {
        super.setItemBackgroundResource(resId)
        mItemBackgroundResId = resId
        applyItemBackgroundResource()
    }

    private fun applyItemBackgroundResource() {
        if (SkinHelper.checkResourceIdValid(mItemBackgroundResId)) {
            return
        }
        itemBackground = SkinCompatVectorResources.getDrawableCompat(context, mItemBackgroundResId) ?: return
    }

    override fun setItemTextAppearance(@StyleRes resId: Int) {
        super.setItemTextAppearance(resId)
        if (resId != INVALID_ID) {
            val a = context.obtainStyledAttributes(resId, R.styleable.SkinTextAppearance)
            if (a.hasValue(R.styleable.SkinTextAppearance_android_textColor)) {
                mTextColorResId = a.getResourceId(R.styleable.SkinTextAppearance_android_textColor, INVALID_ID)
            }
            a.recycle()
            applyItemTextColorResource()
        }
    }

    private fun applyItemTextColorResource() {
        if (SkinHelper.checkResourceIdValid(mTextColorResId)) {
            itemTextColor = SkinResourcesManager.getColorStateList(context, mTextColorResId)
        } else {
            if (SkinHelper.checkResourceIdValid(mDefaultTintResId)) {
                itemTextColor = createDefaultColorStateList(android.R.attr.textColorPrimary)
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

    override fun applySkin() {
        mBackgroundTintHelper.applySkin()
        applyItemIconTintResource()
        applyItemTextColorResource()
        applyItemBackgroundResource()
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
        private val DISABLED_STATE_SET = intArrayOf(-android.R.attr.state_enabled)
    }

}
