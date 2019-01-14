package com.util.skin.library.res

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID

object SkinThemeUtils {

    private val TL_TYPED_VALUE = ThreadLocal<TypedValue>()

    val DISABLED_STATE_SET = intArrayOf(-android.R.attr.state_enabled)
    val ENABLED_STATE_SET = intArrayOf(android.R.attr.state_enabled)
    val WINDOW_FOCUSED_STATE_SET = intArrayOf(android.R.attr.state_window_focused)
    val FOCUSED_STATE_SET = intArrayOf(android.R.attr.state_focused)
    val ACTIVATED_STATE_SET = intArrayOf(android.R.attr.state_activated)
    val ACCELERATED_STATE_SET = intArrayOf(android.R.attr.state_accelerated)
    val HOVERED_STATE_SET = intArrayOf(android.R.attr.state_hovered)
    val DRAG_CAN_ACCEPT_STATE_SET = intArrayOf(android.R.attr.state_drag_can_accept)
    val DRAG_HOVERED_STATE_SET = intArrayOf(android.R.attr.state_drag_hovered)
    val PRESSED_STATE_SET = intArrayOf(android.R.attr.state_pressed)
    val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    val SELECTED_STATE_SET = intArrayOf(android.R.attr.state_selected)
    val NOT_PRESSED_OR_FOCUSED_STATE_SET = intArrayOf(-android.R.attr.state_pressed, -android.R.attr.state_focused)
    val EMPTY_STATE_SET = IntArray(0)

    private val TEMP_ARRAY = IntArray(1)

    private val typedValue: TypedValue
        get() {
            var typedValue = TL_TYPED_VALUE.get()
            if (typedValue == null) {
                typedValue = TypedValue()
                TL_TYPED_VALUE.set(typedValue)
            }
            return typedValue
        }

    fun getTextColorPrimaryResId(context: Context): Int {
        return getResId(context, intArrayOf(android.R.attr.textColorPrimary))
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun getStatusBarColorResId(context: Context): Int {
        return getResId(context, intArrayOf(android.R.attr.statusBarColor))
    }

    fun getWindowBackgroundResId(context: Context): Int {
        return getResId(context, intArrayOf(android.R.attr.windowBackground))
    }

    fun getResId(context: Context, attrs: IntArray): Int {
        val a = context.obtainStyledAttributes(attrs)
        val resId = a.getResourceId(0, INVALID_ID)
        a.recycle()
        return resId
    }

    fun getThemeAttrColor(context: Context, attr: Int): Int {
        TEMP_ARRAY[0] = attr
        val a = context.obtainStyledAttributes(null, TEMP_ARRAY)
        try {
            val resId = a.getResourceId(0, 0)
            return if (resId != 0) {
                SkinResourcesManager.getColor(context, resId)
            } else 0
        } finally {
            a.recycle()
        }
    }

    fun getThemeAttrColorStateList(context: Context, attr: Int): ColorStateList? {
        TEMP_ARRAY[0] = attr
        val a = context.obtainStyledAttributes(null, TEMP_ARRAY)
        try {
            val resId = a.getResourceId(0, 0)
            return if (resId != 0) {
                SkinResourcesManager.getColorStateList(context, resId)
            } else null
        } finally {
            a.recycle()
        }
    }

    fun getDisabledThemeAttrColor(context: Context, attr: Int): Int {
        val csl = getThemeAttrColorStateList(context, attr)
        return if (csl != null && csl.isStateful) {
            // If the CSL is stateful, we'll assume it has a disabled state and use it
            csl.getColorForState(DISABLED_STATE_SET, csl.defaultColor)
        } else {
            // Else, we'll generate the color using disabledAlpha from the theme

            val tv = typedValue
            // Now retrieve the disabledAlpha value from the theme
            context.theme.resolveAttribute(android.R.attr.disabledAlpha, tv, true)
            val disabledAlpha = tv.float

            getThemeAttrColor(context, attr, disabledAlpha)
        }
    }

    internal fun getThemeAttrColor(context: Context, attr: Int, alpha: Float): Int {
        val color = getThemeAttrColor(context, attr)
        val originalAlpha = Color.alpha(color)
        return ColorUtils.setAlphaComponent(color, Math.round(originalAlpha * alpha))
    }
}
