package com.util.skinnable.support.compat.util

import android.content.Context
import com.util.skin.library.res.SkinThemeUtils.getResId

object SkinCompatV7ThemeUtils {

    private val APPCOMPAT_COLOR_PRIMARY_ATTRS = intArrayOf(androidx.appcompat.R.attr.colorPrimary)
    private val APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = intArrayOf(androidx.appcompat.R.attr.colorPrimaryDark)
    private val APPCOMPAT_COLOR_ACCENT_ATTRS = intArrayOf(androidx.appcompat.R.attr.colorAccent)

    fun getColorPrimaryResId(context: Context): Int {
        return getResId(context, APPCOMPAT_COLOR_PRIMARY_ATTRS)
    }

    fun getColorPrimaryDarkResId(context: Context): Int {
        return getResId(context, APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)
    }

    fun getColorAccentResId(context: Context): Int {
        return getResId(context, APPCOMPAT_COLOR_ACCENT_ATTRS)
    }
}
