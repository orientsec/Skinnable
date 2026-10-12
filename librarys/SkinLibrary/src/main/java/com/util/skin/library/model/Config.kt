package com.util.skin.library.model

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.util.skin.library.SkinManager
import com.util.skin.library.app.SkinActivityLifecycle
import com.util.skin.library.factory.SkinFactory
import com.util.skin.library.loader.SkinStrategy
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
    val factories: List<SkinFactory>,
    /**
     * 自定义日志输出
     */
    val logger: com.util.skin.library.utils.SkinLogger?,
    /**
     * 是否自动跟随系统深色模式
     */
    var followSystemDarkMode: Boolean,
    /**
     * 跟随系统浅色模式时的换肤策略
     */
    var systemLightStrategy: SkinStrategy,
    /**
     * 跟随系统深色模式时的换肤策略
     */
    var systemDarkStrategy: SkinStrategy,
    /**
     * 是否开启换肤异常自动降级 Fallback 回退默认皮肤
     */
    val enableAutoFallback: Boolean = true,
    /**
     * 触发降级的连续异常/缺失资源阈值（默认 10 次）
     */
    val maxErrorThreshold: Int = 10,
    /**
     * 换肤全局异常监控与降级回调
     */
    var errorListener: com.util.skin.library.exception.SkinErrorListener? = null
) {
    /**
     * 动态追加 View 膨胀拦截器 / 工厂
     */
    fun addFactory(factory: SkinFactory) {
        if (factories is MutableList<SkinFactory>) {
            if (!factories.contains(factory)) {
                factories.add(factory)
            }
        }
    }
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
        val factories: MutableList<SkinFactory> = mutableListOf(),

        var logger: com.util.skin.library.utils.SkinLogger? = null,

        var followSystemDarkMode: Boolean = false,

        var systemLightStrategy: SkinStrategy = SkinStrategy.BuildIn(""),

        var systemDarkStrategy: SkinStrategy = SkinStrategy.BuildIn("night"),

        var enableAutoFallback: Boolean = true,

        var maxErrorThreshold: Int = 10,

        var errorListener: com.util.skin.library.exception.SkinErrorListener? = null
    ) {
        fun setAllActivity(allActivity: Boolean) = apply { this.allActivity = allActivity }

        fun setWindowBackground(windowBackground: Boolean) =
            apply { this.windowBackground = windowBackground }

        fun addInflater(inflater: SkinFactory) = apply { factories.add(inflater) }

        fun setLogger(logger: com.util.skin.library.utils.SkinLogger) = apply { this.logger = logger }

        fun setFollowSystemDarkMode(
            follow: Boolean,
            lightStrategy: SkinStrategy = SkinStrategy.BuildIn(""),
            darkStrategy: SkinStrategy = SkinStrategy.BuildIn("night")
        ) = apply {
            this.followSystemDarkMode = follow
            this.systemLightStrategy = lightStrategy
            this.systemDarkStrategy = darkStrategy
        }

        fun setEnableAutoFallback(enable: Boolean) = apply { this.enableAutoFallback = enable }

        fun setMaxErrorThreshold(threshold: Int) = apply { this.maxErrorThreshold = threshold }

        fun setErrorListener(listener: com.util.skin.library.exception.SkinErrorListener) = apply { this.errorListener = listener }

        fun build() = SkinConfig(
            allActivity,
            windowBackground,
            factories,
            logger,
            followSystemDarkMode,
            systemLightStrategy,
            systemDarkStrategy,
            enableAutoFallback,
            maxErrorThreshold,
            errorListener
        )
    }

    fun initManager(context: Application): SkinManager {
        _context = context
        SkinManager._config = this
        com.util.skin.library.utils.Slog.logger = logger
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

