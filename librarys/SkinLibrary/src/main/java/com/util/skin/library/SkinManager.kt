package com.util.skin.library

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import com.util.skin.library.loader.SkinLoaderListener
import com.util.skin.library.loader.SkinStrategy
import com.util.skin.library.model.SkinConfig
import com.util.skin.library.model.appContext
import com.util.skin.library.observe.SkinObservable
import com.util.skin.library.observe.SkinState
import com.util.skin.library.res.SkinResourcesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 动态换肤核心管理单例类。
 * 负责皮肤的加载、状态恢复、淡出过渡动画、跟随系统深色模式以及全局换肤事件的观察者分发。
 */
object SkinManager : SkinObservable() {
    internal var _config: SkinConfig? = null
    val config: SkinConfig
        get() = _config ?: throw IllegalArgumentException("config not init, pls init first")

    fun init(config: SkinConfig) {
        _config = config
    }

    private val _skinStateFlow = MutableStateFlow(SkinState())

    /**
     * 提供给 Kotlin Coroutines 或 Jetpack Compose 订阅的响应式 [StateFlow]。
     * 当发生换肤事件时，会实时推送最新的 [SkinState]，以便 UI 层自动响应重绘。
     */
    val skinStateFlow: StateFlow<SkinState> get() = _skinStateFlow.asStateFlow()

    /**
     * 异常监控与埋点跟踪器 [ISkinErrorTracker]
     */
    var errorTracker: com.util.skin.library.fallback.ISkinErrorTracker? = null
        set(value) {
            field = value
            SkinResourcesManager.setErrorTracker(value)
        }

    /**
     * 动态设置全局换肤异常监控与降级兜底监听器
     */
    var errorListener: com.util.skin.library.exception.SkinErrorListener?
        get() = _config?.errorListener
        set(value) {
            _config?.errorListener = value
        }

    internal fun reportError(
        type: com.util.skin.library.exception.SkinErrorType,
        message: String,
        cause: Throwable? = null
    ) {
        errorListener?.onError(type, message, cause)
    }

    internal fun reportFallbackToDefault(reason: String) {
        errorListener?.onFallbackToDefault(reason)
    }

    /**
     * 恢复应用默认主题，清除当前已加载的外部插件皮肤或后缀/前缀配置。
     */
    fun restoreDefaultSkin() {
        SkinResourcesManager.resetDefault()
        notifySkinChanged()
    }

    /**
     * 加载默认的换肤策略（即上次保存并持久化至本地存储的换肤策略）。
     */
    fun loadSkin() {
        SkinResourcesManager.defaultStrategy()?.let { loadSkin(it) }
    }

    /**
     * 根据指定的换肤策略 [SkinStrategy] 同步加载皮肤资源。
     * @param strategy 换肤策略（支持 [SkinStrategy.BuildIn]、[SkinStrategy.PrefixBuildIn]、[SkinStrategy.Assets]、[SkinStrategy.SDCard]）
     * @param listener 换肤过程的回调监听器（可选）
     */
    fun loadSkin(
        strategy: SkinStrategy,
        listener: SkinLoaderListener? = null
    ) {
        addSkinLoader(strategy, listener)
        notifySkinChanged()
    }

    /**
     * 带平滑无缝过渡淡出动画的换肤方法。
     * 1. 捕捉当前 Activity 窗口画面的遮罩快照，防止换肤时瞬间白色闪烁；
     * 2. 在后台无缝加载并切换最新皮肤资源；
     * 3. 渐变淡出快照遮罩 View，实现极致平滑的视觉体验。
     *
     * @param activity 当前显示的 Activity 实例
     * @param strategy 目标换肤策略
     * @param duration 遮罩淡出动画时长（单位：毫秒，默认 300ms）
     * @param listener 换肤过程的回调监听器（可选）
     */
    fun loadSkinWithTransition(
        activity: Activity,
        strategy: SkinStrategy,
        duration: Long = 300L,
        listener: SkinLoaderListener? = null
    ) {
        val decorView = activity.window.decorView as? ViewGroup
        if (decorView == null) {
            loadSkin(strategy, listener)
            return
        }

        try {
            // 截取当前 Window 内容生成 Bitmap 遮罩
            val bitmap = Bitmap.createBitmap(
                if (decorView.width > 0) decorView.width else 1,
                if (decorView.height > 0) decorView.height else 1,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            decorView.draw(canvas)

            val overlayView = ImageView(activity).apply {
                setImageBitmap(bitmap)
                scaleType = ImageView.ScaleType.FIT_XY
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            decorView.addView(overlayView)

            // 执行真实换肤
            loadSkin(strategy, listener)

            // 渐变淡出遮罩 View
            overlayView.animate()
                .alpha(0f)
                .setDuration(duration)
                .withEndAction {
                    decorView.removeView(overlayView)
                    bitmap.recycle()
                }
                .start()
        } catch (e: Exception) {
            com.util.skin.library.utils.Slog.e("SkinManager", "loadSkinWithTransition failed", e)
            loadSkin(strategy, listener)
        }
    }

    /**
     * 设置或切换是否自动跟随系统深色模式
     */
    fun setFollowSystemDarkMode(
        enable: Boolean,
        lightStrategy: SkinStrategy = SkinStrategy.BuildIn(""),
        darkStrategy: SkinStrategy = SkinStrategy.BuildIn("night")
    ) {
        config.followSystemDarkMode = enable
        config.systemLightStrategy = lightStrategy
        config.systemDarkStrategy = darkStrategy
    }

    /**
     * 检查并应用系统深色模式对应的换肤策略
     */
    fun checkAndApplySystemDarkMode(context: Context) {
        if (!config.followSystemDarkMode) return
        val uiMode = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        if (uiMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            loadSkin(config.systemDarkStrategy)
        } else if (uiMode == android.content.res.Configuration.UI_MODE_NIGHT_NO) {
            loadSkin(config.systemLightStrategy)
        }
    }

    fun notifySkinChanged() {
        val strategy = SkinResourcesManager.defaultStrategy()
        _skinStateFlow.value = SkinState(strategy, System.currentTimeMillis())

        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            super.notifyUpdateSkin()
        } else {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                super.notifyUpdateSkin()
            }
        }
    }

    override fun notifyUpdateSkin() {
        notifySkinChanged()
    }

    private fun addSkinLoader(
        strategy: SkinStrategy,
        listener: SkinLoaderListener? = null
    ) {
        listener?.onStart()

        val result = SkinResourcesManager.fallbackManager.executeTransaction(
            targetStrategy = strategy,
            rollbackAction = { previousStrategy ->
                if (previousStrategy != null) {
                    loadTask(previousStrategy)
                } else {
                    SkinResourcesManager.resetDefault()
                }
            },
            block = {
                loadTask(strategy)
                !SkinResourcesManager.isDefaultSkin
            }
        )

        when (result) {
            is com.util.skin.library.fallback.SkinTransactionResult.Success -> {
                listener?.onSuccess()
                listener?.onFinish()
            }
            is com.util.skin.library.fallback.SkinTransactionResult.FailedRolledBack -> {
                val errorMsg = "换肤事务失败并已回滚: ${result.reason}"
                reportError(com.util.skin.library.exception.SkinErrorType.LOAD_FAILED, errorMsg, result.cause)
                reportFallbackToDefault(errorMsg)
                listener?.onFailed(errorMsg)
                listener?.onFinish()
            }
            is com.util.skin.library.fallback.SkinTransactionResult.CircuitBroken -> {
                val errorMsg = "熔断器已锁定: ${result.reason}"
                reportError(com.util.skin.library.exception.SkinErrorType.LOAD_FAILED, errorMsg)
                reportFallbackToDefault(errorMsg)
                listener?.onFailed(errorMsg)
                listener?.onFinish()
            }
        }
    }

    private fun loadTask(strategy: SkinStrategy) {
        strategy.loaderStrategy.initStrategy(appContext)?.let {
            SkinResourcesManager.setupResource(strategy)
        } ?: SkinResourcesManager.resetDefault()
    }
}