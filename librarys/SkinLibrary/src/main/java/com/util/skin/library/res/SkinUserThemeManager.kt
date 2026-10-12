package com.util.skin.library.res

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.util.skin.library.SkinManager
import com.util.skin.library.model.appContext
import com.util.skin.library.res.ColorState.Companion.checkColorValid
import com.util.skin.library.res.ColorState.Companion.toJSONObject
import com.util.skin.library.utils.SkinPreference
import com.util.skin.library.utils.Slog
import com.util.skin.library.utils.getImageRotateAngle
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

/**
 * 用户动态自定义主题管理器。
 * 允许应用在运行时自由设置指定颜色资源（[ColorState] 或十六进制颜色字符串）或图片路径（Drawable Path），
 * 无需制作或加载独立的皮肤包文件。所有配置会自动持久化保存至 SharedPreferences 中。
 */
object SkinUserThemeManager {

    private val mColorNameStateMap = ConcurrentHashMap<String, ColorState>()
    private val mColorCaches = ConcurrentHashMap<Int, WeakReference<ColorStateList>>()
    
    @Volatile
    var isColorEmpty: Boolean = true
        private set

    private val mDrawablePathAndAngleMap = ConcurrentHashMap<String, String>()
    private val mDrawableCaches = ConcurrentHashMap<Int, WeakReference<Drawable>>()
    
    @Volatile
    var isDrawableEmpty: Boolean = true
        private set

    private const val TAG = "SkinCompatUserThemeManager"
    private const val KEY_TYPE = "type"
    private const val KEY_TYPE_COLOR = "color"
    private const val KEY_TYPE_DRAWABLE = "drawable"
    private const val KEY_DRAWABLE_NAME = "drawableName"
    private const val KEY_DRAWABLE_PATH_AND_ANGLE = "drawablePathAndAngle"

    init {
        try {
            startLoadFromSharedPreferences()
        } catch (e: JSONException) {
            mColorNameStateMap.clear()
            mDrawablePathAndAngleMap.clear()
            if (Slog.DEBUG) {
                Slog.i(TAG, "startLoadFromSharedPreferences error: $e")
            }
        }

    }

    private fun checkPathValid(path: String): Boolean {
        val valid = !TextUtils.isEmpty(path) && File(path).exists()
        if (Slog.DEBUG && !valid) {
            Slog.i(TAG, "Invalid drawable path : $path")
        }
        return valid
    }

    @Throws(JSONException::class)
    private fun startLoadFromSharedPreferences() {
        val colors = SkinPreference.userTheme
        if (!TextUtils.isEmpty(colors)) {
            val jsonArray = JSONArray(colors)
            if (Slog.DEBUG) {
                Slog.i(TAG, "startLoadFromSharedPreferences: $jsonArray")
            }
            val count = jsonArray.length()
            for (i in 0 until count) {
                val jsonObject = jsonArray.getJSONObject(i)
                if (jsonObject.has(KEY_TYPE)) {
                    val type = jsonObject.getString(KEY_TYPE)
                    if (KEY_TYPE_COLOR == type) {
                        val state = ColorState.fromJSONObject(jsonObject)
                        if (state != null) {
                            mColorNameStateMap[state.colorName] = state
                        }
                    } else if (KEY_TYPE_DRAWABLE == type) {
                        val drawableName = jsonObject.getString(KEY_DRAWABLE_NAME)
                        val drawablePathAndAngle = jsonObject.getString(KEY_DRAWABLE_PATH_AND_ANGLE)
                        if (!TextUtils.isEmpty(drawableName) && !TextUtils.isEmpty(
                                drawablePathAndAngle
                            )
                        ) {
                            mDrawablePathAndAngleMap[drawableName] = drawablePathAndAngle
                        }
                    }
                }
            }
        }
        
        isColorEmpty = mColorNameStateMap.isEmpty()
        isDrawableEmpty = mDrawablePathAndAngleMap.isEmpty()
    }

    /**
     * 将当前配置的用户自定义主题保存至 SharedPreferences 并触发全局界面皮肤重绘与刷新。
     */
    fun apply() {
        val jsonArray = JSONArray()
        for (colorName in mColorNameStateMap.keys) {
            val state = mColorNameStateMap[colorName]
            if (state != null) {
                try {
                    jsonArray.put(toJSONObject(state).putOpt(KEY_TYPE, KEY_TYPE_COLOR))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
        for (drawableName in mDrawablePathAndAngleMap.keys) {
            val jsonObject = JSONObject()
            try {
                jsonArray.put(
                    jsonObject.putOpt(KEY_TYPE, KEY_TYPE_DRAWABLE)
                        .putOpt(KEY_DRAWABLE_NAME, drawableName)
                        .putOpt(KEY_DRAWABLE_PATH_AND_ANGLE, mDrawablePathAndAngleMap[drawableName])
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        if (Slog.DEBUG) {
            Slog.i(TAG, "Apply user theme: $jsonArray")
        }
        SkinPreference.setUserTheme(jsonArray.toString()).commitEditor()
        SkinManager.notifyUpdateSkin()
    }

    /**
     * 为指定的颜色资源绑定自定义多状态 [ColorState] 对象。
     * @param colorRes 需要替换的颜色资源 ID（例如 R.color.demo_primary）
     * @param state 包含各控件状态颜色的 [ColorState] 对象
     */
    fun addColorState(@ColorRes colorRes: Int, state: ColorState?) {
        val entry = getEntryName(colorRes, KEY_TYPE_COLOR)
        if (!TextUtils.isEmpty(entry) && state != null) {
            state.colorName = entry!!
            mColorNameStateMap[entry] = state
            isColorEmpty = false
            removeColorInCache(colorRes)
        }
    }

    /**
     * 为指定的颜色资源绑定默认十六进制颜色字符串（例如 "#FF3D00"）。
     * @param colorRes 需要替换的颜色资源 ID（例如 R.color.demo_primary）
     * @param colorDefault 十六进制颜色字符串
     */
    fun addColorState(@ColorRes colorRes: Int, colorDefault: String) {
        if (!checkColorValid("colorDefault", colorDefault)) {
            return
        }
        val entry = getEntryName(colorRes, KEY_TYPE_COLOR)
        if (!TextUtils.isEmpty(entry)) {
            mColorNameStateMap[entry!!] = ColorState(entry, colorDefault)
            isColorEmpty = false
            removeColorInCache(colorRes)
        }
    }

    /**
     * 移除指定颜色资源的动态自定义设置，恢复使用默认或皮肤包中的颜色。
     */
    fun removeColorState(@ColorRes colorRes: Int) {
        val entry = getEntryName(colorRes, KEY_TYPE_COLOR)
        if (!TextUtils.isEmpty(entry)) {
            mColorNameStateMap.remove(entry)
            isColorEmpty = mColorNameStateMap.isEmpty()
            removeColorInCache(colorRes)
        }
    }

    internal fun removeColorState(colorName: String) {
        if (!TextUtils.isEmpty(colorName)) {
            mColorNameStateMap.remove(colorName)
            isColorEmpty = mColorNameStateMap.isEmpty()
        }
    }

    fun getColorState(colorName: String): ColorState? {
        return mColorNameStateMap[colorName]
    }

    fun getColorState(@ColorRes colorRes: Int): ColorState? {
        val entry = getEntryName(colorRes, KEY_TYPE_COLOR)
        return if (!TextUtils.isEmpty(entry)) {
            mColorNameStateMap[entry]
        } else null
    }

    fun getColorStateList(@ColorRes colorRes: Int): ColorStateList? {
        var colorStateList = getCachedColor(colorRes)
        if (colorStateList == null) {
            val entry = getEntryName(colorRes, KEY_TYPE_COLOR)
            if (!TextUtils.isEmpty(entry)) {
                val state = mColorNameStateMap[entry]
                if (state != null) {
                    colorStateList = state.parse()
                    if (colorStateList != null) {
                        addColorToCache(colorRes, colorStateList)
                    }
                }
            }
        }
        return colorStateList
    }

    /**
     * 为指定的 Drawable 资源绑定本地图片绝对路径。
     * @param drawableRes 需要替换的 Drawable 资源 ID（例如 R.drawable.ic_logo）
     * @param drawablePath 本地文件系统中图片的绝对路径
     */
    fun addDrawablePath(@DrawableRes drawableRes: Int, drawablePath: String) {
        if (!checkPathValid(drawablePath)) {
            return
        }
        val entry = getEntryName(drawableRes, KEY_TYPE_DRAWABLE)
        if (!TextUtils.isEmpty(entry)) {
            val angle = getImageRotateAngle(drawablePath)
            val drawablePathAndAngle = "$drawablePath:$angle"
            mDrawablePathAndAngleMap[entry!!] = drawablePathAndAngle
            isDrawableEmpty = false
            removeDrawableInCache(drawableRes)
        }
    }

    fun addDrawablePath(@DrawableRes drawableRes: Int, drawablePath: String, angle: Int) {
        if (!checkPathValid(drawablePath)) {
            return
        }
        val entry = getEntryName(drawableRes, KEY_TYPE_DRAWABLE)
        if (!TextUtils.isEmpty(entry)) {
            val drawablePathAndAngle = "$drawablePath:$angle"
            mDrawablePathAndAngleMap[entry!!] = drawablePathAndAngle
            isDrawableEmpty = false
            removeDrawableInCache(drawableRes)
        }
    }

    fun removeDrawablePath(@DrawableRes drawableRes: Int) {
        val entry = getEntryName(drawableRes, KEY_TYPE_DRAWABLE)
        if (!TextUtils.isEmpty(entry)) {
            mDrawablePathAndAngleMap.remove(entry)
            isDrawableEmpty = mDrawablePathAndAngleMap.isEmpty()
            removeDrawableInCache(drawableRes)
        }
    }

    fun getDrawablePath(drawableName: String): String {
        val drawablePathAndAngle = mDrawablePathAndAngleMap[drawableName]
        if (!TextUtils.isEmpty(drawablePathAndAngle)) {
            val splits = drawablePathAndAngle!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            return splits[0]
        }
        return ""
    }

    fun getDrawableAngle(drawableName: String): Int {
        val drawablePathAndAngle = mDrawablePathAndAngleMap[drawableName]
        if (!TextUtils.isEmpty(drawablePathAndAngle)) {
            val splits = drawablePathAndAngle!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (splits.size == 2) {
                return Integer.valueOf(splits[1])
            }
        }
        return 0
    }

    fun getDrawable(@DrawableRes drawableRes: Int): Drawable? {
        var drawable = getCachedDrawable(drawableRes)
        if (drawable == null) {
            val entry = getEntryName(drawableRes, KEY_TYPE_DRAWABLE)
            if (!TextUtils.isEmpty(entry)) {
                val drawablePathAndAngle = mDrawablePathAndAngleMap[entry]
                if (!TextUtils.isEmpty(drawablePathAndAngle)) {
                    val splits = drawablePathAndAngle!!
                        .split(":".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    val path = splits[0]
                    var angle = 0
                    if (splits.size == 2) {
                        angle = Integer.valueOf(splits[1])
                    }
                    if (checkPathValid(path)) {
                        if (angle == 0) {
                            drawable = Drawable.createFromPath(path)
                        } else {
                            val m = Matrix()
                            m.postRotate(angle.toFloat())
                            var bitmap = BitmapFactory.decodeFile(path)
                            bitmap = Bitmap.createBitmap(
                                bitmap, 0, 0,
                                bitmap.width, bitmap.height, m, true
                            )
                            drawable = BitmapDrawable(null, bitmap)
                        }
                        if (drawable != null) {
                            addDrawableToCache(drawableRes, drawable)
                        }
                    }
                }
            }
        }
        return drawable
    }

    /**
     * 清空所有已配置的自定义颜色设置，并立即刷新应用界面。
     */
    fun clearColors() {
        mColorNameStateMap.clear()
        isColorEmpty = true
        clearColorCaches()
        apply()
    }

    /**
     * 清空所有自定义图片路径设置，并立即刷新应用界面。
     */
    fun clearDrawables() {
        mDrawablePathAndAngleMap.clear()
        isDrawableEmpty = true
        clearDrawableCaches()
        apply()
    }

    internal fun clearCaches() {
        clearColorCaches()
        clearDrawableCaches()
    }

    private fun clearColorCaches() {
        mColorCaches.clear()
    }

    private fun clearDrawableCaches() {
        mDrawableCaches.clear()
    }

    private fun getCachedColor(@ColorRes colorRes: Int): ColorStateList? {
        val colorRef = mColorCaches[colorRes]
        if (colorRef != null) {
            val colorStateList = colorRef.get()
            if (colorStateList != null) {
                return colorStateList
            } else {
                mColorCaches.remove(colorRes)
            }
        }
        return null
    }

    private fun addColorToCache(@ColorRes colorRes: Int, colorStateList: ColorStateList?) {
        if (colorStateList != null) {
            mColorCaches[colorRes] = WeakReference(colorStateList)
        }
    }

    private fun removeColorInCache(@ColorRes colorRes: Int) {
        mColorCaches.remove(colorRes)
    }

    private fun getCachedDrawable(@DrawableRes drawableRes: Int): Drawable? {
        val drawableRef = mDrawableCaches[drawableRes]
        if (drawableRef != null) {
            val drawable = drawableRef.get()
            if (drawable != null) {
                return drawable
            } else {
                mDrawableCaches.remove(drawableRes)
            }
        }
        return null
    }

    private fun addDrawableToCache(@DrawableRes drawableRes: Int, drawable: Drawable?) {
        if (drawable != null) {
            mDrawableCaches[drawableRes] = WeakReference(drawable)
        }
    }

    private fun removeDrawableInCache(@DrawableRes drawableRes: Int) {
        mDrawableCaches.remove(drawableRes)
    }

    private fun getEntryName(resId: Int, entryType: String): String? {
        return try {
            val type = appContext.resources.getResourceTypeName(resId)
            if (entryType.equals(type, ignoreCase = true)) {
                appContext.resources.getResourceEntryName(resId)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
