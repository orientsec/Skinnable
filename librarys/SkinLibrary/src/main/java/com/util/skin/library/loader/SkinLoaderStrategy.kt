package com.util.skin.library.loader

import android.content.Context
import com.util.skin.library.model.SkinResource

/**
 * 皮肤包加载策略.
 * @PackageName com.util.skin.library.loader
 * @date 2019/1/10 14:35
 * @author zhanglei
 */
interface SkinLoaderStrategy {
    /**
     * 加载皮肤包.
     *
     * @param context  [Context]
     * @return 加载成功，返回皮肤包名称；失败，则返回空。
     */
    fun initStrategy(context: Context): SkinResource?

    /**
     * 根据应用中的资源ID，获取皮肤包相应资源的资源名.
     *
     * @param context  [Context]
     * @param resId    应用中需要换肤的资源ID.
     * @return 皮肤包中相应的资源名.
     */
    fun getTargetResourceEntryName(context: Context, resId: Int): String?

}