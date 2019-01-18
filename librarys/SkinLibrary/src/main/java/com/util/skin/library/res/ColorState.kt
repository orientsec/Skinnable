package com.util.skin.library.res

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import androidx.annotation.ColorRes
import com.util.skin.library.exception.SkinCompatException
import com.util.skin.library.utils.Slog
import org.json.JSONException
import org.json.JSONObject
import java.util.*

internal class ColorState {
    var isOnlyDefaultColor: Boolean = false
        internal set
    var colorName: String = ""
        internal set
    var colorWindowFocused: String = ""
        internal set
    var colorSelected: String = ""
        internal set
    var colorFocused: String = ""
        internal set
    var colorEnabled: String = ""
        internal set
    var colorPressed: String = ""
        internal set
    var colorChecked: String = ""
        internal set
    var colorActivated: String = ""
        internal set
    var colorAccelerated: String = ""
        internal set
    var colorHovered: String = ""
        internal set
    var colorDragCanAccept: String = ""
        internal set
    var colorDragHovered: String = ""
        internal set
    var colorDefault: String = ""
        internal set

    internal constructor(
        colorWindowFocused: String, colorSelected: String, colorFocused: String,
        colorEnabled: String, colorPressed: String, colorChecked: String, colorActivated: String,
        colorAccelerated: String, colorHovered: String, colorDragCanAccept: String,
        colorDragHovered: String, colorDefault: String
    ) {
        this.colorWindowFocused = colorWindowFocused
        this.colorSelected = colorSelected
        this.colorFocused = colorFocused
        this.colorEnabled = colorEnabled
        this.colorPressed = colorPressed
        this.colorChecked = colorChecked
        this.colorActivated = colorActivated
        this.colorAccelerated = colorAccelerated
        this.colorHovered = colorHovered
        this.colorDragCanAccept = colorDragCanAccept
        this.colorDragHovered = colorDragHovered
        this.colorDefault = colorDefault
        this.isOnlyDefaultColor = (TextUtils.isEmpty(colorWindowFocused)
                && TextUtils.isEmpty(colorSelected)
                && TextUtils.isEmpty(colorFocused)
                && TextUtils.isEmpty(colorEnabled)
                && TextUtils.isEmpty(colorPressed)
                && TextUtils.isEmpty(colorChecked)
                && TextUtils.isEmpty(colorActivated)
                && TextUtils.isEmpty(colorAccelerated)
                && TextUtils.isEmpty(colorHovered)
                && TextUtils.isEmpty(colorDragCanAccept)
                && TextUtils.isEmpty(colorDragHovered))
        if (isOnlyDefaultColor) {
            if (!colorDefault.startsWith("#")) {
                throw SkinCompatException("Default color cannot be a reference, when only default color is available!")
            }
        }
    }

    internal constructor(colorName: String, colorDefault: String) {
        this.colorName = colorName
        this.colorDefault = colorDefault
        this.isOnlyDefaultColor = true
        if (!colorDefault.startsWith("#")) {
            throw SkinCompatException("Default color cannot be a reference, when only default color is available!")
        }
    }

    internal fun parse(): ColorStateList? {
        if (isOnlyDefaultColor) {
            val defaultColor = Color.parseColor(colorDefault)
            return ColorStateList.valueOf(defaultColor)
        }
        return parseAll()
    }

    private fun parseAll(): ColorStateList? {
        var stateColorCount = 0
        val stateSetList = ArrayList<IntArray>()
        val stateColorList = ArrayList<Int>()
        if (!TextUtils.isEmpty(colorWindowFocused)) {
            try {
                val windowFocusedColorStr = getColorStr(colorWindowFocused)
                if (!TextUtils.isEmpty(windowFocusedColorStr)) {
                    val windowFocusedColorInt = Color.parseColor(windowFocusedColorStr)
                    stateSetList.add(SkinThemeUtils.WINDOW_FOCUSED_STATE_SET)
                    stateColorList.add(windowFocusedColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!TextUtils.isEmpty(colorSelected)) {
            try {
                val colorSelectedStr = getColorStr(colorSelected)
                if (!TextUtils.isEmpty(colorSelectedStr)) {
                    val selectedColorInt = Color.parseColor(colorSelectedStr)
                    stateSetList.add(SkinThemeUtils.SELECTED_STATE_SET)
                    stateColorList.add(selectedColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!TextUtils.isEmpty(colorFocused)) {
            try {
                val colorFocusedStr = getColorStr(colorFocused)
                if (!TextUtils.isEmpty(colorFocusedStr)) {
                    val focusedColorInt = Color.parseColor(colorFocusedStr)
                    stateSetList.add(SkinThemeUtils.FOCUSED_STATE_SET)
                    stateColorList.add(focusedColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!TextUtils.isEmpty(colorEnabled)) {
            try {
                val colorEnabledStr = getColorStr(colorEnabled)
                if (!TextUtils.isEmpty(colorEnabledStr)) {
                    val enabledColorInt = Color.parseColor(colorEnabledStr)
                    stateSetList.add(SkinThemeUtils.ENABLED_STATE_SET)
                    stateColorList.add(enabledColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!TextUtils.isEmpty(colorPressed)) {
            try {
                val colorPressedStr = getColorStr(colorPressed)
                if (!TextUtils.isEmpty(colorPressedStr)) {
                    val pressedColorInt = Color.parseColor(colorPressedStr)
                    stateSetList.add(SkinThemeUtils.PRESSED_STATE_SET)
                    stateColorList.add(pressedColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!TextUtils.isEmpty(colorChecked)) {
            try {
                val colorCheckedStr = getColorStr(colorChecked)
                if (!TextUtils.isEmpty(colorCheckedStr)) {
                    val checkedColorInt = Color.parseColor(colorCheckedStr)
                    stateSetList.add(SkinThemeUtils.CHECKED_STATE_SET)
                    stateColorList.add(checkedColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!TextUtils.isEmpty(colorActivated)) {
            try {
                val colorActivatedStr = getColorStr(colorActivated)
                if (!TextUtils.isEmpty(colorActivatedStr)) {
                    val activatedColorInt = Color.parseColor(colorActivatedStr)
                    stateSetList.add(SkinThemeUtils.ACTIVATED_STATE_SET)
                    stateColorList.add(activatedColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!TextUtils.isEmpty(colorAccelerated)) {
            try {
                val colorAcceleratedStr = getColorStr(colorAccelerated)
                if (!TextUtils.isEmpty(colorAcceleratedStr)) {
                    val acceleratedColorInt = Color.parseColor(colorAcceleratedStr)
                    stateSetList.add(SkinThemeUtils.ACCELERATED_STATE_SET)
                    stateColorList.add(acceleratedColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!TextUtils.isEmpty(colorHovered)) {
            try {
                val colorHoveredStr = getColorStr(colorHovered)
                if (!TextUtils.isEmpty(colorHoveredStr)) {
                    val hoveredColorInt = Color.parseColor(colorHoveredStr)
                    stateSetList.add(SkinThemeUtils.HOVERED_STATE_SET)
                    stateColorList.add(hoveredColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!TextUtils.isEmpty(colorDragCanAccept)) {
            try {
                val colorDragCanAcceptStr = getColorStr(colorDragCanAccept)
                if (!TextUtils.isEmpty(colorDragCanAcceptStr)) {
                    val dragCanAcceptColorInt = Color.parseColor(colorDragCanAcceptStr)
                    stateSetList.add(SkinThemeUtils.DRAG_CAN_ACCEPT_STATE_SET)
                    stateColorList.add(dragCanAcceptColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        if (!TextUtils.isEmpty(colorDragHovered)) {
            try {
                val colorDragHoveredStr = getColorStr(colorDragHovered)
                if (!TextUtils.isEmpty(colorDragHoveredStr)) {
                    val dragHoveredColorInt = Color.parseColor(colorDragHoveredStr)
                    stateSetList.add(SkinThemeUtils.DRAG_HOVERED_STATE_SET)
                    stateColorList.add(dragHoveredColorInt)
                    stateColorCount++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        try {
            val colorDefaultStr = getColorStr(colorDefault)
            if (!TextUtils.isEmpty(colorDefaultStr)) {
                val baseColor = Color.parseColor(colorDefaultStr)
                stateSetList.add(SkinThemeUtils.EMPTY_STATE_SET)
                stateColorList.add(baseColor)
                stateColorCount++
            }

            val states = arrayOfNulls<IntArray>(stateColorCount)
            val colors = IntArray(stateColorCount)
            for (index in 0 until stateColorCount) {
                states[index] = stateSetList[index]
                colors[index] = stateColorList[index]
            }
            return ColorStateList(states, colors)
        } catch (e: Exception) {
            e.printStackTrace()
            if (Slog.DEBUG) {
                Slog.i(TAG, "$colorName parse failure.")
            }
            SkinUserThemeManager.removeColorState(colorName)
            return null
        }

    }

    private fun getColorStr(colorName: String): String? {
        if (colorName.startsWith("#")) {
            return colorName
        } else {
            val stateRef = SkinUserThemeManager.getColorState(colorName)
            if (stateRef != null) {
                if (stateRef.isOnlyDefaultColor) {
                    return stateRef.colorDefault
                } else {
                    if (Slog.DEBUG) {
                        Slog.i(TAG, colorName + " cannot reference " + stateRef.colorName)
                    }
                }
            }
        }
        return null
    }

    class ColorBuilder {
        private var colorWindowFocused: String = ""
        private var colorSelected: String = ""
        private var colorFocused: String = ""
        private var colorEnabled: String = ""
        private var colorPressed: String = ""
        private var colorChecked: String = ""
        private var colorActivated: String = ""
        private var colorAccelerated: String = ""
        private var colorHovered: String = ""
        private var colorDragCanAccept: String = ""
        private var colorDragHovered: String = ""
        private var colorDefault: String = ""

        constructor() {}

        constructor(state: ColorState) {
            colorWindowFocused = state.colorWindowFocused
            colorSelected = state.colorSelected
            colorFocused = state.colorFocused
            colorEnabled = state.colorEnabled
            colorPressed = state.colorPressed
            colorChecked = state.colorChecked
            colorActivated = state.colorActivated
            colorAccelerated = state.colorAccelerated
            colorHovered = state.colorHovered
            colorDragCanAccept = state.colorDragCanAccept
            colorDragHovered = state.colorDragHovered
            colorDefault = state.colorDefault
        }

        fun setColorWindowFocused(colorWindowFocused: String): ColorBuilder {
            if (checkColorValid("colorWindowFocused", colorWindowFocused)) {
                this.colorWindowFocused = colorWindowFocused
            }
            return this

        }

        fun setColorWindowFocused(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorWindowFocused = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorSelected(colorSelected: String): ColorBuilder {
            if (checkColorValid("colorSelected", colorSelected)) {
                this.colorSelected = colorSelected
            }
            return this
        }

        fun setColorSelected(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorSelected = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorFocused(colorFocused: String): ColorBuilder {
            if (checkColorValid("colorFocused", colorFocused)) {
                this.colorFocused = colorFocused
            }
            return this
        }

        fun setColorFocused(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorFocused = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorEnabled(colorEnabled: String): ColorBuilder {
            if (checkColorValid("colorEnabled", colorEnabled)) {
                this.colorEnabled = colorEnabled
            }
            return this
        }

        fun setColorEnabled(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorEnabled = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorChecked(colorChecked: String): ColorBuilder {
            if (checkColorValid("colorChecked", colorChecked)) {
                this.colorChecked = colorChecked
            }
            return this
        }

        fun setColorChecked(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorChecked = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorPressed(colorPressed: String): ColorBuilder {
            if (checkColorValid("colorPressed", colorPressed)) {
                this.colorPressed = colorPressed
            }
            return this
        }

        fun setColorPressed(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorPressed = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorActivated(colorActivated: String): ColorBuilder {
            if (checkColorValid("colorActivated", colorActivated)) {
                this.colorActivated = colorActivated
            }
            return this
        }

        fun setColorActivated(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorActivated = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorAccelerated(colorAccelerated: String): ColorBuilder {
            if (checkColorValid("colorAccelerated", colorAccelerated)) {
                this.colorAccelerated = colorAccelerated
            }
            return this
        }

        fun setColorAccelerated(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorAccelerated = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorHovered(colorHovered: String): ColorBuilder {
            if (checkColorValid("colorHovered", colorHovered)) {
                this.colorHovered = colorHovered
            }
            return this
        }

        fun setColorHovered(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorHovered = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorDragCanAccept(colorDragCanAccept: String): ColorBuilder {
            if (checkColorValid("colorDragCanAccept", colorDragCanAccept)) {
                this.colorDragCanAccept = colorDragCanAccept
            }
            return this
        }

        fun setColorDragCanAccept(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorDragCanAccept = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorDragHovered(colorDragHovered: String): ColorBuilder {
            if (checkColorValid("colorDragHovered", colorDragHovered)) {
                this.colorDragHovered = colorDragHovered
            }
            return this
        }

        fun setColorDragHovered(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorDragHovered = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun setColorDefault(colorDefault: String): ColorBuilder {
            if (checkColorValid("colorDefault", colorDefault)) {
                this.colorDefault = colorDefault
            }
            return this
        }

        fun setColorDefault(context: Context, @ColorRes colorRes: Int): ColorBuilder {
            this.colorDefault = context.resources.getResourceEntryName(colorRes)
            return this
        }

        fun build(): ColorState {
            if (TextUtils.isEmpty(colorDefault)) {
                throw SkinCompatException("Default color can not empty!")
            }
            return ColorState(
                colorWindowFocused, colorSelected, colorFocused,
                colorEnabled, colorPressed, colorChecked, colorActivated, colorAccelerated,
                colorHovered, colorDragCanAccept, colorDragHovered, colorDefault
            )
        }
    }

    companion object {
        private const val TAG = "ColorState"

        internal fun checkColorValid(name: String, color: String): Boolean {
            // 不为空
            val colorValid = (!TextUtils.isEmpty(color)
                    // 不以#开始，说明是引用其他颜色值 或者以#开始，则长度必须为7或9
                    && (!color.startsWith("#") || color.length == 7 || color.length == 9))
            if (Slog.DEBUG && !colorValid) {
                Slog.i(TAG, "Invalid color -> $name: $color")
            }
            return colorValid
        }

        @Throws(JSONException::class)
        internal fun toJSONObject(state: ColorState): JSONObject {
            val jsonObject = JSONObject()
            if (state.isOnlyDefaultColor) {
                jsonObject.putOpt("colorName", state.colorName)
                    .putOpt("colorDefault", state.colorDefault)
                    .putOpt("onlyDefaultColor", state.isOnlyDefaultColor)
            } else {
                jsonObject.putOpt("colorName", state.colorName)
                    .putOpt("colorWindowFocused", state.colorWindowFocused)
                    .putOpt("colorSelected", state.colorSelected)
                    .putOpt("colorFocused", state.colorFocused)
                    .putOpt("colorEnabled", state.colorEnabled)
                    .putOpt("colorPressed", state.colorPressed)
                    .putOpt("colorChecked", state.colorChecked)
                    .putOpt("colorActivated", state.colorActivated)
                    .putOpt("colorAccelerated", state.colorAccelerated)
                    .putOpt("colorHovered", state.colorHovered)
                    .putOpt("colorDragCanAccept", state.colorDragCanAccept)
                    .putOpt("colorDragHovered", state.colorDragHovered)
                    .putOpt("colorDefault", state.colorDefault)
                    .putOpt("onlyDefaultColor", state.isOnlyDefaultColor)
            }
            return jsonObject
        }

        internal fun fromJSONObject(jsonObject: JSONObject): ColorState? {
            if (jsonObject.has("colorName")
                && jsonObject.has("colorDefault")
                && jsonObject.has("onlyDefaultColor")
            ) {
                try {
                    val onlyDefaultColor = jsonObject.getBoolean("onlyDefaultColor")
                    val colorName = jsonObject.getString("colorName")
                    val colorDefault = jsonObject.getString("colorDefault")
                    if (onlyDefaultColor) {
                        return ColorState(colorName, colorDefault)
                    } else {
                        val builder = ColorBuilder()
                        builder.setColorDefault(colorDefault)
                        if (jsonObject.has("colorWindowFocused")) {
                            builder.setColorWindowFocused(jsonObject.getString("colorWindowFocused"))
                        }
                        if (jsonObject.has("colorSelected")) {
                            builder.setColorSelected(jsonObject.getString("colorSelected"))
                        }
                        if (jsonObject.has("colorFocused")) {
                            builder.setColorFocused(jsonObject.getString("colorFocused"))
                        }
                        if (jsonObject.has("colorEnabled")) {
                            builder.setColorEnabled(jsonObject.getString("colorEnabled"))
                        }
                        if (jsonObject.has("colorPressed")) {
                            builder.setColorPressed(jsonObject.getString("colorPressed"))
                        }
                        if (jsonObject.has("colorChecked")) {
                            builder.setColorChecked(jsonObject.getString("colorChecked"))
                        }
                        if (jsonObject.has("colorActivated")) {
                            builder.setColorActivated(jsonObject.getString("colorActivated"))
                        }
                        if (jsonObject.has("colorAccelerated")) {
                            builder.setColorAccelerated(jsonObject.getString("colorAccelerated"))
                        }
                        if (jsonObject.has("colorHovered")) {
                            builder.setColorHovered(jsonObject.getString("colorHovered"))
                        }
                        if (jsonObject.has("colorDragCanAccept")) {
                            builder.setColorDragCanAccept(jsonObject.getString("colorDragCanAccept"))
                        }
                        if (jsonObject.has("colorDragHovered")) {
                            builder.setColorDragHovered(jsonObject.getString("colorDragHovered"))
                        }
                        val state = builder.build()
                        state.colorName = colorName
                        return state
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            return null
        }
    }
}
