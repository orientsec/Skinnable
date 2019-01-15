package com.util.skinnable.support.compat.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatSpinner
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.helpers.SkinHelper.Companion.checkResourceIdValid
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.R
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skinnable.support.compat.res.SkinCompatVectorResources

@SuppressLint("PrivateResource")
class SkinCompatSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.spinnerStyle,
    mode: Int = MODE_THEME,
    popupTheme: Resources.Theme? = null
) : AppCompatSpinner(context, attrs, defStyleAttr, mode, popupTheme), SkinSupportable {

    private val mBackgroundTintHelper: SkinBackgroundHelper?
    private var mPopupBackgroundResId = INVALID_ID

    constructor(context: Context, mode: Int) : this(context, null, R.attr.spinnerStyle, mode) {}

    init {
        var mode1 = mode
        val a = context.obtainStyledAttributes(attrs, R.styleable.Spinner, defStyleAttr, 0)

        if (popupContext != null) {
            if (mode1 == MODE_THEME) {
                if (Build.VERSION.SDK_INT >= 11) {
                    // If we're running on API v11+ we will try and read android:spinnerMode
                    var aa: TypedArray? = null
                    try {
                        aa = context.obtainStyledAttributes(
                            attrs, ATTRS_ANDROID_SPINNERMODE,
                            defStyleAttr, 0
                        )
                        if (aa!!.hasValue(0)) {
                            mode1 = aa.getInt(0, MODE_DIALOG)
                        }
                    } catch (e: Exception) {
                        Log.i(TAG, "Could not read android:spinnerMode", e)
                    } finally {
                        aa?.recycle()
                    }
                } else {
                    // Else, we use a default mode of dropdown
                    mode1 = MODE_DROPDOWN
                }
            }

            if (mode1 == MODE_DROPDOWN) {
                val pa = popupContext.obtainStyledAttributes(attrs, R.styleable.Spinner, defStyleAttr, 0)
                mPopupBackgroundResId = pa.getResourceId(R.styleable.Spinner_android_popupBackground, INVALID_ID)
                pa.recycle()
            }
        }
        a.recycle()

        mBackgroundTintHelper = SkinBackgroundHelper(this)
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setPopupBackgroundResource(@DrawableRes resId: Int) {
        super.setPopupBackgroundResource(resId)
        mPopupBackgroundResId = resId
        applyPopupBackground()
    }

    private fun applyPopupBackground() {
        if (checkResourceIdValid(mPopupBackgroundResId)) {
            setPopupBackgroundDrawable(SkinCompatVectorResources.getDrawableCompat(context, mPopupBackgroundResId))
        }
    }

    override fun applySkin() {
        mBackgroundTintHelper?.applySkin()
        applyPopupBackground()
    }

    companion object {
        private val TAG = SkinCompatSpinner::class.java!!.getSimpleName()

        private val ATTRS_ANDROID_SPINNERMODE = intArrayOf(android.R.attr.spinnerMode)

        private const val MODE_DIALOG = 0
        private const val MODE_DROPDOWN = 1
        private const val MODE_THEME = -1
    }

}
