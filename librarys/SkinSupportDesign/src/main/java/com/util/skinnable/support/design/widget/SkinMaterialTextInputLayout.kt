package com.util.skinnable.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.StyleRes
import com.google.android.material.textfield.TextInputLayout
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.helpers.SkinHelper.Companion.INVALID_ID
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.widget.SkinSupportable
import com.util.skinnable.support.compat.helpers.SkinBackgroundHelper
import com.util.skinnable.support.compat.widget.SkinCompatEditText
import com.util.skinnable.support.design.R

@SuppressLint("CustomViewStyleable", "PrivateResource")
class SkinMaterialTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr), SkinSupportable {
    private val mBackgroundTintHelper = SkinBackgroundHelper(this)
    private var mPasswordToggleResId = INVALID_ID
    private var mCounterTextColorResId = INVALID_ID
    private var mErrorTextColorResId = INVALID_ID
    private var mFocusedTextColorResId = INVALID_ID
    private var mDefaultTextColorResId = INVALID_ID
    override val skinnable: Boolean by lazy {
        return@lazy SkinResourcesManager.parseSkinnable(
            context,
            attrs,
            defStyleAttr
        )
    }

    private val counterView: TextView?
        get() {
            try {
                val counterView = TextInputLayout::class.java.getDeclaredField("mCounterView")
                counterView.isAccessible = true
                return counterView.get(this) as TextView?
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

    private val errorView: TextView?
        get() {
            try {
                val errorView = TextInputLayout::class.java.getDeclaredField("mErrorView")
                errorView.isAccessible = true
                return errorView.get(this) as TextView?
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

    init {
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)

        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.TextInputLayout,
            defStyleAttr,
            R.style.Widget_Design_TextInputLayout
        )
        if (a.hasValue(R.styleable.TextInputLayout_android_textColorHint)) {
            mFocusedTextColorResId = a.getResourceId(R.styleable.TextInputLayout_android_textColorHint, INVALID_ID)
            mDefaultTextColorResId = mFocusedTextColorResId
            applyFocusedTextColorResource()
        }

        val errorTextAppearance = a.getResourceId(R.styleable.TextInputLayout_errorTextAppearance, INVALID_ID)
        loadErrorTextColorResFromAttributes(errorTextAppearance)
        val counterTextAppearance = a.getResourceId(R.styleable.TextInputLayout_counterTextAppearance, INVALID_ID)
        loadCounterTextColorResFromAttributes(counterTextAppearance)
        mPasswordToggleResId = a.getResourceId(R.styleable.TextInputLayout_passwordToggleDrawable, INVALID_ID)
        a.recycle()
    }

    private fun loadCounterTextColorResFromAttributes(@StyleRes resId: Int) {
        if (resId != INVALID_ID) {
            val counterTA = context.obtainStyledAttributes(resId, R.styleable.SkinTextAppearance)
            if (counterTA.hasValue(R.styleable.SkinTextAppearance_android_textColor)) {
                mCounterTextColorResId = counterTA.getResourceId(
                    R.styleable.SkinTextAppearance_android_textColor,
                    INVALID_ID
                )
            }
            counterTA.recycle()
        }
        applyCounterTextColorResource()
    }

    override fun setCounterEnabled(enabled: Boolean) {
        super.setCounterEnabled(enabled)
        if (enabled) {
            applyCounterTextColorResource()
        }
    }

    private fun applyCounterTextColorResource() {
        if (SkinHelper.checkResourceIdValid(mCounterTextColorResId)) {
            val counterView = counterView
            if (counterView != null) {
                counterView.setTextColor(SkinResourcesManager.getColor(context, mCounterTextColorResId))
                updateEditTextBackground()
            }
        }
    }

    override fun setErrorTextAppearance(@StyleRes resId: Int) {
        super.setErrorTextAppearance(resId)
        loadErrorTextColorResFromAttributes(resId)
    }

    private fun loadErrorTextColorResFromAttributes(@StyleRes resId: Int) {
        if (resId != INVALID_ID) {
            val errorTA = context.obtainStyledAttributes(resId, R.styleable.SkinTextAppearance)
            if (errorTA.hasValue(R.styleable.SkinTextAppearance_android_textColor)) {
                mErrorTextColorResId =
                        errorTA.getResourceId(R.styleable.SkinTextAppearance_android_textColor, INVALID_ID)
            }
            errorTA.recycle()
        }
        applyErrorTextColorResource()
    }

    override fun setErrorEnabled(enabled: Boolean) {
        super.setErrorEnabled(enabled)
        if (enabled) {
            applyErrorTextColorResource()
        }
    }

    private fun applyErrorTextColorResource() {
        if (SkinHelper.checkResourceIdValid(mErrorTextColorResId)
            && mErrorTextColorResId != R.color.design_error
        ) {
            val errorView = errorView
            if (errorView != null) {
                errorView.setTextColor(SkinResourcesManager.getColor(context, mErrorTextColorResId))
                updateEditTextBackground()
            }
        }
    }

    private fun updateEditTextBackground() {
        try {
            val updateEditTextBackground = TextInputLayout::class.java.getDeclaredMethod("updateEditTextBackground")
            updateEditTextBackground.isAccessible = true
            updateEditTextBackground.invoke(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setDefaultTextColor(colors: ColorStateList) {
        try {
            val defaultTextColor = TextInputLayout::class.java.getDeclaredField("mDefaultTextColor")
            defaultTextColor.isAccessible = true
            defaultTextColor.set(this, colors)
            updateLabelState()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun applyFocusedTextColorResource() {
        if (SkinHelper.checkResourceIdValid(mFocusedTextColorResId) && mFocusedTextColorResId != R.color.abc_hint_foreground_material_light) {
            setFocusedTextColor(SkinResourcesManager.getColorStateList(context, mFocusedTextColorResId))
        } else if (editText != null) {
            var textColorResId = INVALID_ID
            if (editText is SkinCompatEditText) {
                textColorResId = (editText as SkinCompatEditText).textColorResId
            } else if (editText is SkinMaterialTextInputEditText) {
                textColorResId = (editText as SkinMaterialTextInputEditText).textColorResId
            }
            if (SkinHelper.checkResourceIdValid(textColorResId)) {
                val colors = SkinResourcesManager.getColorStateList(context, textColorResId)
                setFocusedTextColor(colors)
            }
        }
    }

    private fun setFocusedTextColor(colors: ColorStateList?) {
        try {
            val focusedTextColor = TextInputLayout::class.java.getDeclaredField("mFocusedTextColor")
            focusedTextColor.isAccessible = true
            focusedTextColor.set(this, colors)
            updateLabelState()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun updateLabelState() {
        try {
            val updateLabelState =
                TextInputLayout::class.java.getDeclaredMethod("updateLabelState", Boolean::class.javaPrimitiveType!!)
            updateLabelState.isAccessible = true
            updateLabelState.invoke(this, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun applySkin() {
        applyErrorTextColorResource()
        applyCounterTextColorResource()
        applyFocusedTextColorResource()
        mBackgroundTintHelper.applySkin()
    }

}
