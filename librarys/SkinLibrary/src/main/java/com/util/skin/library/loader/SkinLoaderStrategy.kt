package com.util.skin.library.loader

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable

/**
 * 皮肤包加载策略.
 * @PackageName com.util.skin.library.loader
 * @date 2019/1/10 14:35
 * @author zhanglei
 */
interface SkinLoaderStrategy {
    /**
     * [.SKIN_LOADER_STRATEGY_NONE]
     * [.SKIN_LOADER_STRATEGY_ASSETS]
     * [.SKIN_LOADER_STRATEGY_BUILD_IN]
     * [.SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN]
     *
     * @return 皮肤包加载策略类型.
     */
    val type: SkinLoaderStrategyType

    /**
     * 加载皮肤包.
     *
     * @param context  [Context]
     * @param skinName 皮肤包名称.
     * @return 加载成功，返回皮肤包名称；失败，则返回空。
     */
    fun loadSkinInBackground(context: Context, skinName: String): String?

    /**
     * 根据应用中的资源ID，获取皮肤包相应资源的资源名.
     *
     * @param context  [Context]
     * @param skinName 皮肤包名称.
     * @param resId    应用中需要换肤的资源ID.
     * @return 皮肤包中相应的资源名.
     */
    fun getTargetResourceEntryName(context: Context, skinName: String, resId: Int): String?

    /**
     * 开发者可以拦截应用中的资源ID，返回对应color值。
     *
     * @param context  [Context]
     * @param skinName 皮肤包名称.
     * @param resId    应用中需要换肤的资源ID.
     * @return 获得拦截后的颜色值，添加到ColorStateList的defaultColor中。不需要拦截，则返回空
     */
    fun getColor(context: Context, skinName: String, resId: Int): ColorStateList?

    /**
     * 开发者可以拦截应用中的资源ID，返回对应ColorStateList。
     *
     * @param context  [Context]
     * @param skinName 皮肤包名称.
     * @param resId    应用中需要换肤的资源ID.
     * @return 返回对应ColorStateList。不需要拦截，则返回空
     */
    fun getColorStateList(context: Context, skinName: String, resId: Int): ColorStateList?

    /**
     * 开发者可以拦截应用中的资源ID，返回对应Drawable。
     *
     * @param context  [Context]
     * @param skinName 皮肤包名称.
     * @param resId    应用中需要换肤的资源ID.
     * @return 返回对应Drawable。不需要拦截，则返回空
     */
    fun getDrawable(context: Context, skinName: String, resId: Int): Drawable?
}