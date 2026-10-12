package com.util.skinnable.support.compat.res

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatDelegate
import com.util.skin.library.res.SkinResources
import com.util.skin.library.res.SkinResourcesManager
import com.util.skinnable.support.compat.util.SkinCompatDrawableManager
import com.util.skinnable.support.compat.util.SkinCompatDrawableUtils

object SkinCompatVectorResources : SkinResources {

    init {
        SkinResourcesManager.addSkinResources(this)
    }

    override fun clear() {
        SkinCompatDrawableManager.clearCaches()
    }

    private fun getSkinDrawableCompat(context: Context, resId: Int): Drawable? {
        var drawable: Drawable? = null
        if (AppCompatDelegate.isCompatVectorFromResourcesEnabled()) {
            try {
                drawable = SkinCompatDrawableManager.getDrawable(context, resId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (drawable == null) {
            drawable = SkinResourcesManager.getDrawable(context, resId)
        }
        return drawable?.let { prepareVectorDrawableForSkin(it) }
    }

    /**
     * 针对 VectorDrawable 在低版本 Android 上的着色缓存（ConstantState）污染与不刷新问题进行适配处理。
     * 通过安全 mutate() 隔离 ConstantState，清除旧 ColorFilter，并确保独立刷新。
     */
    fun prepareVectorDrawableForSkin(drawable: Drawable): Drawable {
        var mutated = drawable
        if (SkinCompatDrawableUtils.canSafelyMutateDrawable(mutated)) {
            mutated = mutated.mutate()
        }
        // 清除旧皮肤残余的 ColorFilter，避免 ConstantState 或实例状态污染
        mutated.clearColorFilter()
        SkinCompatDrawableUtils.fixDrawable(mutated)
        return mutated
    }

    fun getDrawableCompat(context: Context, resId: Int): Drawable? {
        return getSkinDrawableCompat(context, resId)
    }
}
