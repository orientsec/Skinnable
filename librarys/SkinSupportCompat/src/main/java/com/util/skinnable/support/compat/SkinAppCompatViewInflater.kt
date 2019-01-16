package com.util.skinnable.support.compat

import android.annotation.SuppressLint
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
import com.util.skin.library.app.SkinLayoutInflater
import com.util.skin.library.app.SkinWrapper
import com.util.skin.library.utils.Slog
import com.util.skinnable.support.compat.widget.*

@SuppressLint("PrivateResource")
class SkinAppCompatViewInflater : SkinLayoutInflater, SkinWrapper {

    override fun createView(context: Context, name: String, attrs: AttributeSet): View? {
        return createViewFromFV(context, name, attrs) ?: createViewFromV7(context, name, attrs)
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

    @SuppressLint("RestrictedApi")
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
            tempContext = themeContext(tempContext, attrs, isPre21, readAppTheme)
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
                } else if (viewParent === windowDecor
                    || viewParent !is View
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

    /**
     * Allows us to emulate the `android:theme` attribute for devices before L.
     */
    private fun themeContext(
        context: Context, attrs: AttributeSet,
        useAndroidTheme: Boolean, useAppTheme: Boolean
    ): Context {
        var context1 = context
        val a = context1.obtainStyledAttributes(attrs, R.styleable.View, 0, 0)
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

        if (themeId != 0 && (context1 !is ContextThemeWrapper || context1.themeResId != themeId)) {
            // If the context isn't a ContextThemeWrapper, or it is but does not have
            // the same theme as we need, wrap it in a new wrapper
            context1 = ContextThemeWrapper(context1, themeId)
        }
        return context1
    }

    companion object {
        private const val LOG_TAG = "SkinAppCompatViewInflater"
    }

}
