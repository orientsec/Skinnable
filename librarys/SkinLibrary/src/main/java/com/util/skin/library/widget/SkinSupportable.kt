package com.util.skin.library.widget

interface SkinSupportable {
    /**
     * 是否支持换肤
     */
    val skinnable: Boolean

    /**
     * 换肤回调
     */
    fun applySkin()
}
