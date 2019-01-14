package com.util.skinnable.support.compat.util

import android.graphics.drawable.*
import android.os.Build
import com.util.skin.library.res.SkinThemeUtils
import com.util.skin.library.utils.SkinCompatVersionUtils

internal object SkinCompatDrawableUtils {

    private const val VECTOR_DRAWABLE_CLAZZ_NAME = "android.graphics.drawable.VectorDrawable"

    /**
     * Attempt the fix any issues in the given drawable, usually caused by platform bugs in the
     * implementation. This method should be call after retrieval from
     * [android.content.res.Resources] or a [android.content.res.TypedArray].
     */
    fun fixDrawable(drawable: Drawable) {
        if (Build.VERSION.SDK_INT == 21 && VECTOR_DRAWABLE_CLAZZ_NAME == drawable.javaClass.getName()) {
            fixVectorDrawableTinting(drawable)
        }
    }

    /**
     * Some drawable implementations have problems with mutation. This method returns false if
     * there is a known issue in the given drawable's implementation.
     */
    fun canSafelyMutateDrawable(drawable: Drawable): Boolean {
        if (Build.VERSION.SDK_INT < 15 && drawable is InsetDrawable) {
            return false
        } else if (Build.VERSION.SDK_INT < 15 && drawable is GradientDrawable) {
            // GradientDrawable has a bug pre-ICS which results in mutate() resulting
            // in loss of color
            return false
        } else if (Build.VERSION.SDK_INT < 17 && drawable is LayerDrawable) {
            return false
        }

        if (drawable is DrawableContainer) {
            // If we have a DrawableContainer, let's traverse it's child array
            val state = drawable.getConstantState()
            if (state is DrawableContainer.DrawableContainerState) {
                for (child in state.children) {
                    if (!canSafelyMutateDrawable(child)) {
                        return false
                    }
                }
            }
        } else if (SkinCompatVersionUtils.isV4DrawableWrapper(drawable)) {
            return canSafelyMutateDrawable(SkinCompatVersionUtils.getV4DrawableWrapperWrappedDrawable(drawable))
        } else if (SkinCompatVersionUtils.isV4WrappedDrawable(drawable)) {
            return canSafelyMutateDrawable(SkinCompatVersionUtils.getV4WrappedDrawableWrappedDrawable(drawable))
        } else if (SkinCompatVersionUtils.isV7DrawableWrapper(drawable)) {
            return canSafelyMutateDrawable(SkinCompatVersionUtils.getV7DrawableWrapperWrappedDrawable(drawable))
        } else if (drawable is ScaleDrawable) {
            val scaleDrawable = drawable.drawable
            if (scaleDrawable != null) {
                return canSafelyMutateDrawable(scaleDrawable)
            }
        }

        return true
    }

    /**
     * VectorDrawable has an issue on API 21 where it sometimes doesn't create its tint filter.
     * Fixed by toggling it's state to force a filter creation.
     */
    private fun fixVectorDrawableTinting(drawable: Drawable) {
        val originalState = drawable.state
        if (originalState.isEmpty()) {
            // The drawable doesn't have a state, so set it to be checked
            drawable.state = SkinThemeUtils.CHECKED_STATE_SET
        } else {
            // Else the drawable does have a state, so clear it
            drawable.state = SkinThemeUtils.EMPTY_STATE_SET
        }
        // Now set the original state
        drawable.state = originalState
    }
}
