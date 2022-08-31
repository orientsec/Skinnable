package com.util.skinnable.support.compat

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.TintContextWrapper
import androidx.appcompat.widget.VectorEnabledTintResources
import com.util.skin.library.factory.SkinFactory
import com.util.skinnable.support.compat.widget.*

@SuppressLint("PrivateResource")
class SkinAppCompatViewFactory : SkinFactory {

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val wrapContext = wrapContext(context, attrs)
        return createViewFromFV(wrapContext, name, attrs) ?: createViewFromV7(
            wrapContext,
            name,
            attrs
        )
    }

    private fun createViewFromFV(context: Context, name: String, attrs: AttributeSet): View? {
        if (name.contains(".")) {
            return null
        }
        return when (name) {
            "View" -> SkinCompatView(context, attrs)
            "LinearLayout" -> SkinCompatLinearLayout(context, attrs)
            "RelativeLayout" -> SkinCompatRelativeLayout(context, attrs)
            "FrameLayout" -> SkinCompatFrameLayout(context, attrs)
            "TextView" -> SkinCompatTextView(context, attrs)
            "ImageView" -> SkinCompatImageView(context, attrs)
            "Button" -> SkinCompatButton(context, attrs)
            "EditText" -> SkinCompatEditText(context, attrs)
            "Spinner" -> SkinCompatSpinner(context, attrs)
            "ImageButton" -> SkinCompatImageButton(context, attrs)
            "CheckBox" -> SkinCompatCheckBox(context, attrs)
            "RadioButton" -> SkinCompatRadioButton(context, attrs)
            "RadioGroup" -> SkinCompatRadioGroup(context, attrs)
            "CheckedTextView" -> SkinCompatCheckedTextView(context, attrs)
            "AutoCompleteTextView" -> SkinCompatAutoCompleteTextView(context, attrs)
            "MultiAutoCompleteTextView" -> SkinCompatMultiAutoCompleteTextView(context, attrs)
            "RatingBar" -> SkinCompatRatingBar(context, attrs)
            "SeekBar" -> SkinCompatSeekBar(context, attrs)
            "ProgressBar" -> SkinCompatProgressBar(context, attrs)
            "ScrollView" -> SkinCompatScrollView(context, attrs)
            else -> null
        }
    }

    private fun createViewFromV7(context: Context, name: String, attrs: AttributeSet): View? {
        return when (name) {
            "androidx.appcompat.widget.Toolbar" -> SkinCompatToolbar(context, attrs)
            else -> null
        }
    }

    private fun wrapContext(context: Context, attrs: AttributeSet): Context {
        return if (VectorEnabledTintResources.shouldBeUsed()) {
            TintContextWrapper.wrap(context)
        } else {
            themeContext(context, attrs)
        }
    }

    /**
     * Allows us to emulate the `android:theme` attribute for devices before L.
     */
    private fun themeContext(context: Context, attrs: AttributeSet): Context {
        var context1 = context
        val a = context1.obtainStyledAttributes(attrs, R.styleable.View, 0, 0)
        // ...if that didn't work, try reading app:theme (for legacy reasons) if enabled
        val themeId: Int = a.getResourceId(R.styleable.View_theme, 0)
        if (themeId != 0) {
            Log.i(
                LOG_TAG,
                "app:theme is now deprecated. " + "Please move to using android:theme instead."
            )
        }
        a.recycle()

        if (themeId != 0 && (context1 !is ContextThemeWrapper || context1.themeResId != themeId)) {
            // If the context isn't a ContextThemeWrapper, or it is but does not have
            // the same theme as we need, wrap it in a new wrapper
            context1 = ContextThemeWrapper(context1, themeId)
        }
        return context1
    }

    companion object {
        private const val LOG_TAG = "SkinViewInflater"
    }

}
