package com.util.skinnable.support.compat.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.ConstantState
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.collection.ArrayMap
import androidx.collection.LongSparseArray
import androidx.collection.LruCache
import androidx.core.graphics.ColorUtils.compositeColors
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.res.SkinThemeUtils
import com.util.skin.library.res.SkinThemeUtils.getDisabledThemeAttrColor
import com.util.skin.library.res.SkinThemeUtils.getThemeAttrColor
import com.util.skin.library.res.SkinThemeUtils.getThemeAttrColorStateList
import com.util.skinnable.support.compat.R
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.lang.ref.WeakReference
import java.util.*

@SuppressLint("PrivateResource")
internal object SkinCompatDrawableManager {

    init {
        installDefaultInflateDelegates()
    }

    private val TAG = SkinCompatDrawableManager::class.java.simpleName
    private const val DEBUG = false
    private val DEFAULT_MODE = PorterDuff.Mode.SRC_IN
    private const val SKIP_DRAWABLE_TAG = "appcompat_skip_skip"

    private const val PLATFORM_VD_CLAZZ = "android.graphics.drawable.VectorDrawable"

    private var mTintLists: WeakHashMap<Context, SparseArray<ColorStateList>>? = null
    private var mDelegates: ArrayMap<String, InflateDelegate>? = null
    private var mKnownDrawableIdTags: SparseArray<String>? = null

    private val mDrawableCacheLock = Any()
    private val mDrawableCaches = WeakHashMap<Context, LongSparseArray<WeakReference<ConstantState>>>(0)

    private var mTypedValue: TypedValue? = null

    private var mHasCheckedVectorDrawableSetup: Boolean = false

    private interface InflateDelegate {
        fun createFromXmlInner(
            context: Context, parser: XmlPullParser,
            attrs: AttributeSet, theme: Resources.Theme?
        ): Drawable?
    }

    fun clearCaches() {
        mDrawableCaches.clear()
        if (mKnownDrawableIdTags != null) {
            mKnownDrawableIdTags!!.clear()
        }
        if (mTintLists != null) {
            mTintLists!!.clear()
        }
        COLOR_FILTER_CACHE.evictAll()
    }

    fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable? {
        return getDrawable(context, resId, false)
    }

    private fun getDrawable(
        context: Context, @DrawableRes resId: Int,
        failIfNotKnown: Boolean
    ): Drawable? {
        checkVectorDrawableSetup(context)

        var drawable = loadDrawableFromDelegates(context, resId)
        if (drawable == null) {
            drawable = createDrawableIfNeeded(context, resId)
        }
        if (drawable == null) {
            drawable = SkinResourcesManager.getDrawable(context, resId)
        }

        if (drawable != null) {
            // Tint it if needed
            drawable = tintDrawable(context, resId, failIfNotKnown, drawable)
        }
        if (drawable != null) {
            // See if we need to 'fix' the drawable
            SkinCompatDrawableUtils.fixDrawable(drawable)
        }
        return drawable
    }

    fun onConfigurationChanged(context: Context) {
        synchronized(mDrawableCacheLock) {
            val cache = mDrawableCaches[context]
            cache?.clear()
        }
    }

    private fun createDrawableIfNeeded(
        context: Context,
        @DrawableRes resId: Int
    ): Drawable? {
        if (mTypedValue == null) {
            mTypedValue = TypedValue()
        }
        mTypedValue?.let { tv ->
            SkinResourcesManager.getValue(context, resId, tv, true)
            val key = createCacheKey(tv)

            var dr = getCachedDrawable(context, key)
            if (dr != null) {
                // If we got a cached drawable, return it
                return dr
            }

            // Else we need to try and create one...
            if (resId == androidx.appcompat.R.drawable.abc_cab_background_top_material) {
                dr = LayerDrawable(
                    arrayOf(
                        getDrawable(
                            context,
                            com.util.skinnable.support.compat.R.drawable.abc_cab_background_internal_bg
                        ),
                        getDrawable(
                            context,
                            com.util.skinnable.support.compat.R.drawable.abc_cab_background_top_mtrl_alpha
                        )
                    )
                )
            }

            if (dr != null) {
                dr.changingConfigurations = tv.changingConfigurations
                // If we reached here then we created a new drawable, add it to the cache
                addDrawableToCache(context, key, dr)
            }

            return dr
        }
    }

    private fun tintDrawable(
        context: Context, @DrawableRes resId: Int,
        failIfNotKnown: Boolean, drawable: Drawable
    ): Drawable? {
        var drawable1 = drawable
        val tintList = getTintList(context, resId)
        if (tintList != null) {
            // First mutate the Drawable, then wrap it and set the tint list
            if (SkinCompatDrawableUtils.canSafelyMutateDrawable(drawable1)) {
                drawable1 = drawable1.mutate()
            }
            drawable1 = DrawableCompat.wrap(drawable1)
            DrawableCompat.setTintList(drawable1, tintList)

            // If there is a blending mode specified for the drawable, use it
            val tintMode = getTintMode(resId)
            if (tintMode != null) {
                DrawableCompat.setTintMode(drawable1, tintMode)
            }
        } else if (resId == androidx.appcompat.R.drawable.abc_seekbar_track_material) {
            val ld = drawable1 as LayerDrawable
            setPorterDuffColorFilter(
                ld.findDrawableByLayerId(android.R.id.background),
                getThemeAttrColor(context, com.util.skinnable.support.compat.R.attr.colorControlNormal), DEFAULT_MODE
            )
            setPorterDuffColorFilter(
                ld.findDrawableByLayerId(android.R.id.secondaryProgress),
                getThemeAttrColor(context, com.util.skinnable.support.compat.R.attr.colorControlNormal), DEFAULT_MODE
            )
            setPorterDuffColorFilter(
                ld.findDrawableByLayerId(android.R.id.progress),
                getThemeAttrColor(context, com.util.skinnable.support.compat.R.attr.colorControlActivated), DEFAULT_MODE
            )
        } else if (resId == androidx.appcompat.R.drawable.abc_ratingbar_material
            || resId == androidx.appcompat.R.drawable.abc_ratingbar_indicator_material
            || resId == androidx.appcompat.R.drawable.abc_ratingbar_small_material
        ) {
            val ld = drawable1 as LayerDrawable
            setPorterDuffColorFilter(
                ld.findDrawableByLayerId(android.R.id.background),
                getDisabledThemeAttrColor(context, com.util.skinnable.support.compat.R.attr.colorControlNormal),
                DEFAULT_MODE
            )
            setPorterDuffColorFilter(
                ld.findDrawableByLayerId(android.R.id.secondaryProgress),
                getThemeAttrColor(context, com.util.skinnable.support.compat.R.attr.colorControlActivated), DEFAULT_MODE
            )
            setPorterDuffColorFilter(
                ld.findDrawableByLayerId(android.R.id.progress),
                getThemeAttrColor(context, R.attr.colorControlActivated), DEFAULT_MODE
            )
        } else {
            val tinted = tintDrawableUsingColorFilter(context, resId, drawable1)
            if (!tinted && failIfNotKnown) {
                // If we didn't tint using a ColorFilter, and we're set to fail if we don't
                // know the id, return null
                return null
            }
        }
        return drawable1
    }

    private fun loadDrawableFromDelegates(context: Context, @DrawableRes resId: Int): Drawable? {
        if (mDelegates != null && !mDelegates!!.isEmpty) {
            if (mKnownDrawableIdTags != null) {
                val cachedTagName = mKnownDrawableIdTags!!.get(resId)
                if (SKIP_DRAWABLE_TAG == cachedTagName || cachedTagName != null && mDelegates!![cachedTagName] == null) {
                    // If we don't have a delegate for the drawable tag, or we've been set to
                    // skip it, fail fast and return null
                    if (DEBUG) {
                        Log.d(
                            TAG,
                            "[loadDrawableFromDelegates] Skipping drawable: " + context.resources.getResourceName(resId)
                        )
                    }
                    return null
                }
            } else {
                // Create an id cache as we'll need one later
                mKnownDrawableIdTags = SparseArray()
            }

            if (mTypedValue == null) {
                mTypedValue = TypedValue()
            }
            mTypedValue?.let { tv ->
                SkinResourcesManager.getValue(context, resId, tv, true)

                val key = createCacheKey(tv)

                var dr = getCachedDrawable(context, key)
                if (dr != null) {
                    if (DEBUG) {
                        Log.i(
                            TAG,
                            "[loadDrawableFromDelegates] Returning cached drawable: " + context.resources.getResourceName(
                                resId
                            )
                        )
                    }
                    // We have a cached drawable, return it!
                    return dr
                }

                if (tv.string != null && tv.string.toString().endsWith(".xml")) {
                    // If the resource is an XML file, let's try and parse it
                    try {
                        val parser = SkinResourcesManager.getXml(context, resId)
                        val attrs = Xml.asAttributeSet(parser)
                        var type = parser.next()
                        while (type != XmlPullParser.START_TAG
                            && type != XmlPullParser.END_DOCUMENT
                        ) {
                            type = parser.next()
                            // Empty loop
                        }
                        if (type != XmlPullParser.START_TAG) {
                            throw XmlPullParserException("No start tag found")
                        }

                        val tagName = parser.name
                        // Add the tag name to the cache
                        mKnownDrawableIdTags!!.append(resId, tagName)

                        // Now try and find a delegate for the tag name and inflate if found
                        val delegate = mDelegates!![tagName]
                        if (delegate != null) {
                            dr = delegate.createFromXmlInner(context, parser, attrs, null)
                        }
                        if (dr != null) {
                            // Add it to the drawable cache
                            dr.changingConfigurations = tv.changingConfigurations
                            if (addDrawableToCache(context, key, dr) && DEBUG) {
                                Log.i(
                                    TAG,
                                    "[loadDrawableFromDelegates] Saved drawable to cache: " + context.resources.getResourceName(
                                        resId
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e(TAG, "Exception while inflating drawable", e)
                    }

                }
                if (dr == null) {
                    // If we reach here then the delegate inflation of the resource failed. Mark it as
                    // bad so we skip the id next time
                    mKnownDrawableIdTags!!.append(resId, SKIP_DRAWABLE_TAG)
                }
                return dr
            }
        }

        return null
    }

    private fun getCachedDrawable(context: Context, key: Long): Drawable? {
        synchronized(mDrawableCacheLock) {
            val cache = mDrawableCaches[context] ?: return null

            val wr = cache.get(key)
            if (wr != null) {
                // We have the key, and the secret
                val entry = wr.get()
                if (entry != null) {
                    return entry.newDrawable(context.resources)
                } else {
                    // Our entry has been purged
                    cache.delete(key)
                }
            }
        }
        return null
    }

    private fun addDrawableToCache(
        context: Context, key: Long,
        drawable: Drawable
    ): Boolean {
        val cs = drawable.constantState
        if (cs != null) {
            synchronized(mDrawableCacheLock) {
                var cache: LongSparseArray<WeakReference<ConstantState>>? = mDrawableCaches[context]
                if (cache == null) {
                    cache = LongSparseArray()
                    mDrawableCaches[context] = cache
                }
                cache.put(key, WeakReference(cs))
            }
            return true
        }
        return false
    }

    private fun addDelegate(tagName: String, delegate: InflateDelegate) {
        if (mDelegates == null) {
            mDelegates = ArrayMap()
        }
        mDelegates!![tagName] = delegate
    }

    private fun removeDelegate(tagName: String, delegate: InflateDelegate) {
        if (mDelegates != null && mDelegates!![tagName] === delegate) {
            mDelegates!!.remove(tagName)
        }
    }

    private fun getTintList(context: Context, @DrawableRes resId: Int): ColorStateList? {
        // Try the cache first (if it exists)
        var tint = getTintListFromCache(context, resId)

        if (tint == null) {
            // ...if the cache did not contain a color state list, try and create one
            if (resId == androidx.appcompat.R.drawable.abc_edit_text_material) {
                tint = SkinResourcesManager.getColorStateList(context, androidx.appcompat.R.color.abc_tint_edittext)
            } else if (resId == androidx.appcompat.R.drawable.abc_switch_track_mtrl_alpha) {
                tint = SkinResourcesManager.getColorStateList(context, androidx.appcompat.R.color.abc_tint_switch_track)
            } else if (resId == androidx.appcompat.R.drawable.abc_switch_thumb_material) {
                tint = createSwitchThumbColorStateList(context)
            } else if (resId == androidx.appcompat.R.drawable.abc_btn_default_mtrl_shape) {
                tint = createDefaultButtonColorStateList(context)
            } else if (resId == androidx.appcompat.R.drawable.abc_btn_borderless_material) {
                tint = createBorderlessButtonColorStateList(context)
            } else if (resId == androidx.appcompat.R.drawable.abc_btn_colored_material) {
                tint = createColoredButtonColorStateList(context)
            } else if (resId == androidx.appcompat.R.drawable.abc_spinner_mtrl_am_alpha || resId == androidx.appcompat.R.drawable.abc_spinner_textfield_background_material) {
                tint = SkinResourcesManager.getColorStateList(context, androidx.appcompat.R.color.abc_tint_spinner)
            } else if (arrayContains(TINT_COLOR_CONTROL_NORMAL, resId)) {
                tint = getThemeAttrColorStateList(context, androidx.appcompat.R.attr.colorControlNormal)
            } else if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, resId)) {
                tint = SkinResourcesManager.getColorStateList(context, androidx.appcompat.R.color.abc_tint_default)
            } else if (arrayContains(TINT_CHECKABLE_BUTTON_LIST, resId)) {
                tint = SkinResourcesManager.getColorStateList(
                    context,
                    androidx.appcompat.R.color.abc_tint_btn_checkable
                )
            } else if (resId == androidx.appcompat.R.drawable.abc_seekbar_thumb_material) {
                tint = SkinResourcesManager.getColorStateList(context, androidx.appcompat.R.color.abc_tint_seek_thumb)
            }

            if (tint != null) {
                addTintListToCache(context, resId, tint)
            }
        }
        return tint
    }

    private fun getTintListFromCache(context: Context, @DrawableRes resId: Int): ColorStateList? {
        if (mTintLists != null) {
            val tints = mTintLists!![context]
            return tints?.get(resId)
        }
        return null
    }

    private fun addTintListToCache(
        context: Context, @DrawableRes resId: Int,
        tintList: ColorStateList
    ) {
        if (mTintLists == null) {
            mTintLists = WeakHashMap()
        }
        var themeTints: SparseArray<ColorStateList>? = mTintLists!![context]
        if (themeTints == null) {
            themeTints = SparseArray()
            mTintLists!![context] = themeTints
        }
        themeTints.append(resId, tintList)
    }

    private fun createDefaultButtonColorStateList(context: Context): ColorStateList {
        return createButtonColorStateList(
            context,
            getThemeAttrColor(context, R.attr.colorButtonNormal)
        )
    }

    private fun createBorderlessButtonColorStateList(context: Context): ColorStateList {
        // We ignore the custom tint for borderless buttons
        return createButtonColorStateList(context, Color.TRANSPARENT)
    }

    private fun createColoredButtonColorStateList(context: Context): ColorStateList {
        return createButtonColorStateList(
            context,
            getThemeAttrColor(context, R.attr.colorAccent)
        )
    }

    private fun createButtonColorStateList(
        context: Context,
        @ColorInt baseColor: Int
    ): ColorStateList {
        val states = arrayOfNulls<IntArray>(4)
        val colors = IntArray(4)
        var i = 0

        val colorControlHighlight = getThemeAttrColor(context, androidx.appcompat.R.attr.colorControlHighlight)
        val disabledColor = getDisabledThemeAttrColor(context, androidx.appcompat.R.attr.colorButtonNormal)

        // Disabled state
        states[i] = SkinThemeUtils.DISABLED_STATE_SET
        colors[i] = disabledColor
        i++

        states[i] = SkinThemeUtils.PRESSED_STATE_SET
        colors[i] = compositeColors(colorControlHighlight, baseColor)
        i++

        states[i] = SkinThemeUtils.FOCUSED_STATE_SET
        colors[i] = compositeColors(colorControlHighlight, baseColor)
        i++

        // Default enabled state
        states[i] = SkinThemeUtils.EMPTY_STATE_SET
        colors[i] = baseColor

        return ColorStateList(states, colors)
    }

    private fun createSwitchThumbColorStateList(context: Context): ColorStateList {
        val states = arrayOfNulls<IntArray>(3)
        val colors = IntArray(3)
        var i = 0

        val thumbColor = SkinThemeUtils.getThemeAttrColorStateList(
            context,
            androidx.appcompat.R.attr.colorSwitchThumbNormal
        )

        if (thumbColor != null && thumbColor.isStateful) {
            // If colorSwitchThumbNormal is a valid ColorStateList, extract the default and
            // disabled colors from it

            // Disabled state
            states[i] = SkinThemeUtils.DISABLED_STATE_SET
            colors[i] = thumbColor.getColorForState(states[i], 0)
            i++

            states[i] = SkinThemeUtils.CHECKED_STATE_SET
            colors[i] = SkinThemeUtils.getThemeAttrColor(context, androidx.appcompat.R.attr.colorControlActivated)
            i++

            // Default enabled state
            states[i] = SkinThemeUtils.EMPTY_STATE_SET
            colors[i] = thumbColor.defaultColor
        } else {
            // Else we'll use an approximation using the default disabled alpha

            // Disabled state
            states[i] = SkinThemeUtils.DISABLED_STATE_SET
            colors[i] = SkinThemeUtils.getDisabledThemeAttrColor(
                context,
                androidx.appcompat.R.attr.colorSwitchThumbNormal
            )
            i++

            states[i] = SkinThemeUtils.CHECKED_STATE_SET
            colors[i] = SkinThemeUtils.getThemeAttrColor(context, androidx.appcompat.R.attr.colorControlActivated)
            i++

            // Default enabled state
            states[i] = SkinThemeUtils.EMPTY_STATE_SET
            colors[i] =
                    SkinThemeUtils.getThemeAttrColor(context, androidx.appcompat.R.attr.colorSwitchThumbNormal)
        }

        return ColorStateList(states, colors)
    }

    private class ColorFilterLruCache(maxSize: Int) : LruCache<Int, PorterDuffColorFilter>(maxSize) {

        internal operator fun get(color: Int, mode: PorterDuff.Mode): PorterDuffColorFilter? {
            return get(generateCacheKey(color, mode))
        }

        internal fun put(color: Int, mode: PorterDuff.Mode, filter: PorterDuffColorFilter): PorterDuffColorFilter? {
            return put(generateCacheKey(color, mode), filter)
        }

        private fun generateCacheKey(color: Int, mode: PorterDuff.Mode): Int {
            var hashCode = 1
            hashCode = 31 * hashCode + color
            hashCode = 31 * hashCode + mode.hashCode()
            return hashCode
        }
    }

    private fun checkVectorDrawableSetup(context: Context) {
        if (mHasCheckedVectorDrawableSetup) {
            // We've already checked so return now...
            return
        }
        // Here we will check that a known Vector drawable resource inside AppCompat can be
        // correctly decoded
        mHasCheckedVectorDrawableSetup = true
        val d = getDrawable(context, R.drawable.abc_vector_test)
        if (d == null || !isVectorDrawable(d)) {
            mHasCheckedVectorDrawableSetup = false
            throw IllegalStateException("This app has been built with an incorrect " + "configuration. Please configure your build for VectorDrawableCompat.")
        }
    }

    private class VdcInflateDelegate internal constructor() : InflateDelegate {

        @SuppressLint("NewApi")
        override fun createFromXmlInner(
            context: Context, parser: XmlPullParser,
            attrs: AttributeSet, theme: Resources.Theme?
        ): Drawable? {
            return try {
                VectorDrawableCompat
                    .createFromXmlInner(context.resources, parser, attrs, theme)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("VdcInflateDelegate", "Exception while inflating <vector>", e)
                null
            }

        }
    }

    @RequiresApi(11)
    @TargetApi(11)
    private class AvdcInflateDelegate internal constructor() : InflateDelegate {

        @SuppressLint("NewApi")
        override fun createFromXmlInner(
            context: Context, parser: XmlPullParser,
            attrs: AttributeSet, theme: Resources.Theme?
        ): Drawable? {
            return try {
                AnimatedVectorDrawableCompat
                    .createFromXmlInner(context, context.resources, parser, attrs, theme)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("AvdcInflateDelegate", "Exception while inflating <animated-vector>", e)
                null
            }

        }
    }


    private fun installDefaultInflateDelegates() {
        // This sdk version check will affect src:appCompat code path.
        // Although VectorDrawable exists in Android framework from Lollipop, AppCompat will use the
        // VectorDrawableCompat before Nougat to utilize the bug fixes in VectorDrawableCompat.
        if (Build.VERSION.SDK_INT < 24) {
            addDelegate("vector", VdcInflateDelegate())
            // AnimatedVectorDrawableCompat only works on API v11+
            addDelegate("animated-vector", AvdcInflateDelegate())
        }
    }

    private val COLOR_FILTER_CACHE = ColorFilterLruCache(6)

    /**
     * Drawables which should be tinted with the value of `R.attr.colorControlNormal`,
     * using the default mode using a raw color filter.
     */
    private val COLORFILTER_TINT_COLOR_CONTROL_NORMAL = intArrayOf(
        androidx.appcompat.R.drawable.abc_textfield_search_default_mtrl_alpha,
        androidx.appcompat.R.drawable.abc_textfield_default_mtrl_alpha,
        androidx.appcompat.R.drawable.abc_ab_share_pack_mtrl_alpha
    )

    /**
     * Drawables which should be tinted with the value of `R.attr.colorControlNormal`, using
     * [DrawableCompat]'s tinting functionality.
     */
    private val TINT_COLOR_CONTROL_NORMAL = intArrayOf(
        androidx.appcompat.R.drawable.abc_ic_commit_search_api_mtrl_alpha,
        androidx.appcompat.R.drawable.abc_seekbar_tick_mark_material,
        androidx.appcompat.R.drawable.abc_ic_menu_share_mtrl_alpha,
        androidx.appcompat.R.drawable.abc_ic_menu_copy_mtrl_am_alpha,
        androidx.appcompat.R.drawable.abc_ic_menu_cut_mtrl_alpha,
        androidx.appcompat.R.drawable.abc_ic_menu_selectall_mtrl_alpha,
        androidx.appcompat.R.drawable.abc_ic_menu_paste_mtrl_am_alpha
    )

    /**
     * Drawables which should be tinted with the value of `R.attr.colorControlActivated`,
     * using a color filter.
     */
    private val COLORFILTER_COLOR_CONTROL_ACTIVATED = intArrayOf(
        androidx.appcompat.R.drawable.abc_textfield_activated_mtrl_alpha,
        androidx.appcompat.R.drawable.abc_textfield_search_activated_mtrl_alpha,
        androidx.appcompat.R.drawable.abc_cab_background_top_mtrl_alpha,
        androidx.appcompat.R.drawable.abc_text_cursor_material,
        androidx.appcompat.R.drawable.abc_text_select_handle_left_mtrl_dark,
        androidx.appcompat.R.drawable.abc_text_select_handle_middle_mtrl_dark,
        androidx.appcompat.R.drawable.abc_text_select_handle_right_mtrl_dark,
        androidx.appcompat.R.drawable.abc_text_select_handle_left_mtrl_light,
        androidx.appcompat.R.drawable.abc_text_select_handle_middle_mtrl_light,
        androidx.appcompat.R.drawable.abc_text_select_handle_right_mtrl_light
    )

    /**
     * Drawables which should be tinted with the value of `android.R.attr.colorBackground`,
     * using the [PorterDuff.Mode.MULTIPLY] mode and a color filter.
     */
    private val COLORFILTER_COLOR_BACKGROUND_MULTIPLY = intArrayOf(
        androidx.appcompat.R.drawable.abc_popup_background_mtrl_mult,
        androidx.appcompat.R.drawable.abc_cab_background_internal_bg,
        androidx.appcompat.R.drawable.abc_menu_hardkey_panel_mtrl_mult
    )

    /**
     * Drawables which should be tinted using a state list containing values of
     * `R.attr.colorControlNormal` and `R.attr.colorControlActivated`
     */
    private val TINT_COLOR_CONTROL_STATE_LIST = intArrayOf(
        androidx.appcompat.R.drawable.abc_tab_indicator_material,
        androidx.appcompat.R.drawable.abc_textfield_search_material
    )

    /**
     * Drawables which should be tinted using a state list containing values of
     * `R.attr.colorControlNormal` and `R.attr.colorControlActivated` for the checked
     * state.
     */
    private val TINT_CHECKABLE_BUTTON_LIST = intArrayOf(
        androidx.appcompat.R.drawable.abc_btn_check_material,
        androidx.appcompat.R.drawable.abc_btn_radio_material
    )

    private fun createCacheKey(tv: TypedValue): Long {
        return tv.assetCookie.toLong() shl 32 or tv.data.toLong()
    }

    fun tintDrawableUsingColorFilter(
        context: Context,
        @DrawableRes resId: Int, drawable: Drawable
    ): Boolean {
        var drawable1 = drawable
        var tintMode = DEFAULT_MODE
        var colorAttrSet = false
        var colorAttr = 0
        var alpha = -1

        when {
            arrayContains(COLORFILTER_TINT_COLOR_CONTROL_NORMAL, resId) -> {
                colorAttr = androidx.appcompat.R.attr.colorControlNormal
                colorAttrSet = true
            }
            arrayContains(COLORFILTER_COLOR_CONTROL_ACTIVATED, resId) -> {
                colorAttr = androidx.appcompat.R.attr.colorControlActivated
                colorAttrSet = true
            }
            arrayContains(COLORFILTER_COLOR_BACKGROUND_MULTIPLY, resId) -> {
                colorAttr = android.R.attr.colorBackground
                colorAttrSet = true
                tintMode = PorterDuff.Mode.MULTIPLY
            }
            resId == androidx.appcompat.R.drawable.abc_list_divider_mtrl_alpha -> {
                colorAttr = android.R.attr.colorForeground
                colorAttrSet = true
                alpha = Math.round(0.16f * 255)
            }
            resId == androidx.appcompat.R.drawable.abc_dialog_material_background -> {
                colorAttr = android.R.attr.colorBackground
                colorAttrSet = true
            }
        }

        if (colorAttrSet) {
            if (SkinCompatDrawableUtils.canSafelyMutateDrawable(drawable1)) {
                drawable1 = drawable1.mutate()
            }

            val color = getThemeAttrColor(context, colorAttr)
            drawable1.colorFilter = getPorterDuffColorFilter(color, tintMode)

            if (alpha != -1) {
                drawable1.alpha = alpha
            }

            if (DEBUG) {
                Log.d(
                    TAG, "[tintDrawableUsingColorFilter] Tinted "
                            + context.resources.getResourceName(resId) +
                            " with color: #" + Integer.toHexString(color)
                )
            }
            return true
        }
        return false
    }

    private fun arrayContains(array: IntArray, value: Int): Boolean {
        for (id in array) {
            if (id == value) {
                return true
            }
        }
        return false
    }

    fun getTintMode(resId: Int): PorterDuff.Mode? {
        var mode: PorterDuff.Mode? = null

        if (resId == androidx.appcompat.R.drawable.abc_switch_thumb_material) {
            mode = PorterDuff.Mode.MULTIPLY
        }

        return mode
    }

    private fun createTintFilter(
        tint: ColorStateList?,
        tintMode: PorterDuff.Mode?, state: IntArray
    ): PorterDuffColorFilter? {
        if (tint == null || tintMode == null) {
            return null
        }
        val color = tint.getColorForState(state, Color.TRANSPARENT)
        return getPorterDuffColorFilter(color, tintMode)
    }

    fun getPorterDuffColorFilter(color: Int, mode: PorterDuff.Mode): PorterDuffColorFilter {
        // First, lets see if the cache already contains the color filter
        var filter = COLOR_FILTER_CACHE[color, mode]

        if (filter == null) {
            // Cache miss, so create a color filter and add it to the cache
            filter = PorterDuffColorFilter(color, mode)
            COLOR_FILTER_CACHE.put(color, mode, filter)
        }

        return filter
    }

    private fun setPorterDuffColorFilter(d: Drawable, color: Int, mode: PorterDuff.Mode?) {
        var drawable = d
        if (SkinCompatDrawableUtils.canSafelyMutateDrawable(drawable)) {
            drawable = drawable.mutate()
        }
        drawable.colorFilter = getPorterDuffColorFilter(color, mode ?: DEFAULT_MODE)
    }

    private fun isVectorDrawable(d: Drawable): Boolean {
        return d is VectorDrawableCompat || PLATFORM_VD_CLAZZ == d.javaClass.getName()
    }
}
