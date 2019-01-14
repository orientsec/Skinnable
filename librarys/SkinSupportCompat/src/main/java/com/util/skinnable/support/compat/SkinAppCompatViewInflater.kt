package com.util.skinnable.support.compat

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.TintContextWrapper
import androidx.appcompat.widget.VectorEnabledTintResources
import androidx.core.view.ViewCompat
import com.util.skin.library.R
import com.util.skin.library.app.SkinLayoutInflater
import com.util.skin.library.app.SkinWrapper
import com.util.skin.library.utils.Slog
import com.util.skinnable.support.compat.widget.SkinCompatAutoCompleteTextView
import com.util.skinnable.support.compat.widget.SkinCompatButton
import com.util.skinnable.support.compat.widget.SkinCompatCheckBox
import com.util.skinnable.support.compat.widget.SkinCompatCheckedTextView
import com.util.skinnable.support.compat.widget.SkinCompatEditText
import com.util.skinnable.support.compat.widget.SkinCompatFrameLayout
import com.util.skinnable.support.compat.widget.SkinCompatImageButton
import com.util.skinnable.support.compat.widget.SkinCompatImageView
import com.util.skinnable.support.compat.widget.SkinCompatLinearLayout
import com.util.skinnable.support.compat.widget.SkinCompatMultiAutoCompleteTextView
import com.util.skinnable.support.compat.widget.SkinCompatProgressBar
import com.util.skinnable.support.compat.widget.SkinCompatRadioButton
import com.util.skinnable.support.compat.widget.SkinCompatRadioGroup
import com.util.skinnable.support.compat.widget.SkinCompatRatingBar
import com.util.skinnable.support.compat.widget.SkinCompatRelativeLayout
import com.util.skinnable.support.compat.widget.SkinCompatScrollView
import com.util.skinnable.support.compat.widget.SkinCompatSeekBar
import com.util.skinnable.support.compat.widget.SkinCompatSpinner
import com.util.skinnable.support.compat.widget.SkinCompatTextView
import com.util.skinnable.support.compat.widget.SkinCompatToolbar
import com.util.skinnable.support.compat.widget.SkinCompatView

class SkinAppCompatViewInflater : SkinLayoutInflater, SkinWrapper {

    override fun createView(context: Context, name: String, attrs: AttributeSet): View? {
        return createViewFromFV(context, name, attrs) ?: createViewFromV7(context, name, attrs)
    }

    private fun createViewFromFV(context: Context, name: String, attrs: AttributeSet): View? {
        var view: View? = null
        if (name.contains(".")) {
            return null
        }
        when (name) {
            "View" -> view = SkinCompatView(context, attrs)
            "LinearLayout" -> view = SkinCompatLinearLayout(context, attrs)
            "RelativeLayout" -> view = SkinCompatRelativeLayout(context, attrs)
            "FrameLayout" -> view = SkinCompatFrameLayout(context, attrs)
            "TextView" -> view = SkinCompatTextView(context, attrs)
            "ImageView" -> view = SkinCompatImageView(context, attrs)
            "Button" -> view = SkinCompatButton(context, attrs)
            "EditText" -> view = SkinCompatEditText(context, attrs)
            "Spinner" -> view = SkinCompatSpinner(context, attrs)
            "ImageButton" -> view = SkinCompatImageButton(context, attrs)
            "CheckBox" -> view = SkinCompatCheckBox(context, attrs)
            "RadioButton" -> view = SkinCompatRadioButton(context, attrs)
            "RadioGroup" -> view = SkinCompatRadioGroup(context, attrs)
            "CheckedTextView" -> view = SkinCompatCheckedTextView(context, attrs)
            "AutoCompleteTextView" -> view = SkinCompatAutoCompleteTextView(context, attrs)
            "MultiAutoCompleteTextView" -> view = SkinCompatMultiAutoCompleteTextView(context, attrs)
            "RatingBar" -> view = SkinCompatRatingBar(context, attrs)
            "SeekBar" -> view = SkinCompatSeekBar(context, attrs)
            "ProgressBar" -> view = SkinCompatProgressBar(context, attrs)
            "ScrollView" -> view = SkinCompatScrollView(context, attrs)
            else -> {
            }
        }
        return view
    }

    private fun createViewFromV7(context: Context, name: String, attrs: AttributeSet): View? {
        var view: View? = null
        when (name) {
            "androidx.appcompat.widget.Toolbar" -> view = SkinCompatToolbar(context, attrs)
            else -> {
            }
        }
        return view
    }

    override fun wrapContext(context: Context, parent: View?, attrs: AttributeSet): Context {
        var tempContext = context
        val isPre21 = Build.VERSION.SDK_INT < 21

        // We only want the View to inherit its context if we're running pre-v21
        val inheritContext = isPre21 && shouldInheritContext(tempContext, parent as ViewParent?)

        // We can emulate Lollipop's android:theme attribute propagating down the view hierarchy
        // by using the parent's context
        if (inheritContext && parent != null) {
            tempContext = parent.context
        }
        val readAppTheme = true /* Read read app:theme as a fallback at all times for legacy reasons */
        val wrapContext = VectorEnabledTintResources.shouldBeUsed() /* Only tint wrap the context if enabled */

        // We can emulate Lollipop's android:theme attribute propagating down the view hierarchy
        // by using the parent's context
        if (inheritContext && parent != null) {
            tempContext = parent.context
        }
        if (isPre21 || readAppTheme) {
            // We then apply the theme on the context, if specified
            tempContext = themifyContext(tempContext, attrs, isPre21, readAppTheme)
        }
        if (wrapContext) {
            tempContext = TintContextWrapper.wrap(tempContext)
        }
        return tempContext
    }

    private fun shouldInheritContext(context: Context, parent: ViewParent?): Boolean {
        var viewParent: ViewParent? = parent
            ?: // The initial parent is null so just return false
            return false
        if (context is Activity) {
            val windowDecor = context.window.decorView
            while (true) {
                if (viewParent == null) {
                    // Bingo. We've hit a view which has a null parent before being terminated from
                    // the loop. This is (most probably) because it's the root view in an inflation
                    // call, therefore we should inherit. This works as the inflated layout is only
                    // added to the hierarchy at the end of the inflate() call.
                    return true
                } else if (viewParent === windowDecor || viewParent !is View
                    || ViewCompat.isAttachedToWindow((viewParent as View?)!!)
                ) {
                    // We have either hit the window's decor view, a parent which isn't a View
                    // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                    // is currently added to the view hierarchy. This means that it has not be
                    // inflated in the current inflate() call and we should not inherit the context.
                    return false
                }
                viewParent = viewParent!!.parent
            }
        }
        return false
    }

    companion object {
        private val LOG_TAG = "SkinAppCompatViewInflater"

        /**
         * Allows us to emulate the `android:theme` attribute for devices before L.
         */
        private fun themifyContext(
            context: Context, attrs: AttributeSet,
            useAndroidTheme: Boolean, useAppTheme: Boolean
        ): Context {
            var context = context
            val a = context.obtainStyledAttributes(attrs, R.styleable.View, 0, 0)
            var themeId = 0
            if (useAndroidTheme) {
                // First try reading android:theme if enabled
                themeId = a.getResourceId(R.styleable.View_android_theme, 0)
            }
            if (useAppTheme && themeId == 0) {
                // ...if that didn't work, try reading app:theme (for legacy reasons) if enabled
                themeId = a.getResourceId(R.styleable.View_theme, 0)

                if (themeId != 0) {
                    Slog.i(LOG_TAG, "app:theme is now deprecated. " + "Please move to using android:theme instead.")
                }
            }
            a.recycle()

            if (themeId != 0 && (context !is ContextThemeWrapper || context.themeResId != themeId)) {
                // If the context isn't a ContextThemeWrapper, or it is but does not have
                // the same theme as we need, wrap it in a new wrapper
                context = ContextThemeWrapper(context, themeId)
            }
            return context
        }
    }

}
