package com.util.skin.library.model

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.util.skin.library.SkinManager
import com.util.skin.library.app.SkinActivityLifecycle
import com.util.skin.library.factory.SkinFactory
import com.util.skin.library.utils.SkinPreference

/**
 * @PackageName com.util.skin.library.model
 * @date 2022/8/26 15:39
 * @author zhanglei
 */

class SkinConfig private constructor(
    /**
     * 是否支持所有页面
     */
    val allActivity: Boolean,
    /**
     * window背景色是否支持换肤
     */
    val windowBackground: Boolean,
    /**
     * 换肤
     */
    val factories: List<SkinFactory>
) {
    data class Builder(
        /**
         * 是否支持所有页面
         */
        var allActivity: Boolean = true,
        /**
         * window背景色是否支持换肤
         */
        var windowBackground: Boolean = false,
        /**
         * 换肤
         */
        val factories: MutableList<SkinFactory> = mutableListOf()
    ) {
        fun setAllActivity(allActivity: Boolean) = apply { this.allActivity = allActivity }

        fun setWindowBackground(windowBackground: Boolean) =
            apply { this.windowBackground = windowBackground }

        fun addInflater(inflater: SkinFactory) = apply { factories.add(inflater) }

        fun build() = SkinConfig(allActivity, windowBackground, factories)
    }

    fun initManager(context: Application): SkinManager {
        _context = context
        SkinManager._config = this
        SkinPreference.init(context)
        SkinActivityLifecycle.init(context)
        return SkinManager
    }

    companion object {
        fun builder() = Builder()
    }
}


@SuppressLint("StaticFieldLeak")
internal var _context: Context? = null
internal val appContext: Context
    get() = _context!!

