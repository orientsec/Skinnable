package com.util.skin.library.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.util.skin.library.R
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.res.SkinResourcesManager

private class SkinBinderHolder(val binder: () -> Unit)

/**
 * 针对代码动态设置（Programmatic Styling）的 View 动态换肤绑定 DSL。
 * 当换肤发生时，此方法会被 [applySkinToViewTree] 自动触发并重新执行绑定的 lambda。
 */
@Suppress("UNCHECKED_CAST")
fun View.applySkinBinders() {
    val binders = getTag(R.id.skin_binders_tag) as? ArrayList<SkinBinderHolder>
    binders?.forEach { holder ->
        holder.binder()
    }
}

private fun View.addSkinBinderHolder(holder: SkinBinderHolder) {
    @Suppress("UNCHECKED_CAST")
    var binders = getTag(R.id.skin_binders_tag) as? ArrayList<SkinBinderHolder>
    if (binders == null) {
        binders = ArrayList()
        setTag(R.id.skin_binders_tag, binders)
    }
    binders.add(holder)
}

/**
 * 针对代码动态设置（Programmatic Styling）的 View 动态换肤绑定扩展。
 * 动态为 View 绑定颜色资源 ID，每次全局换肤发生时会自动重新解析皮肤包中的最新 Color 值并回调 [binder]。
 *
 * @param colorResId 目标颜色资源 ID
 * @param binder 换肤回调 Lambda，入参为解析到的最新颜色值 (Color Int)
 */
fun <T : View> T.bindSkinColor(@ColorRes colorResId: Int, binder: T.(color: Int) -> Unit): T {
    val holder = SkinBinderHolder {
        if (SkinHelper.checkResourceIdValid(colorResId)) {
            val color = SkinResourcesManager.getColor(context, colorResId)
            this.binder(color)
        }
    }
    addSkinBinderHolder(holder)
    holder.binder()
    return this
}

/**
 * 针对代码动态设置的 View 换肤绑定扩展。
 * 动态为 View 绑定 [ColorStateList] 资源 ID，换肤时自动重新解析并回调 [binder]。
 *
 * @param colorResId 目标 ColorStateList 资源 ID
 * @param binder 换肤回调 Lambda，入参为解析到的最新 ColorStateList
 */
fun <T : View> T.bindSkinColorStateList(@ColorRes colorResId: Int, binder: T.(csl: ColorStateList?) -> Unit): T {
    val holder = SkinBinderHolder {
        if (SkinHelper.checkResourceIdValid(colorResId)) {
            val csl = SkinResourcesManager.getColorStateList(context, colorResId)
            this.binder(csl)
        }
    }
    addSkinBinderHolder(holder)
    holder.binder()
    return this
}

/**
 * 针对代码动态设置的 View 换肤绑定扩展。
 * 动态为 View 绑定 Drawable 资源 ID，每次全局换肤发生时会自动重新解析皮肤包中的最新 Drawable 并回调 [binder]。
 *
 * @param drawableResId 目标 Drawable 资源 ID
 * @param binder 换肤回调 Lambda，入参为解析到的最新 Drawable 对象
 */
fun <T : View> T.bindSkinDrawable(@DrawableRes drawableResId: Int, binder: T.(drawable: Drawable?) -> Unit): T {
    val holder = SkinBinderHolder {
        if (SkinHelper.checkResourceIdValid(drawableResId)) {
            val drawable = SkinResourcesManager.getDrawable(context, drawableResId)
            this.binder(drawable)
        }
    }
    addSkinBinderHolder(holder)
    holder.binder()
    return this
}

/**
 * 通用代码动态换肤逻辑绑定 DSL。
 * 换肤触发时会自动重新执行 [binder] 内的代码块，适合处理复杂的自定义逻辑。
 *
 * @param binder 换肤发生时需要重新执行的代码块
 */
fun <T : View> T.bindSkinCustom(binder: T.() -> Unit): T {
    val holder = SkinBinderHolder {
        this.binder()
    }
    addSkinBinderHolder(holder)
    holder.binder()
    return this
}