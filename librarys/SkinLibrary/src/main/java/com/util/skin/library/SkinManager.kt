package com.util.skin.library

import com.util.skin.library.loader.SkinLoaderListener
import com.util.skin.library.loader.SkinStrategy
import com.util.skin.library.model.SkinConfig
import com.util.skin.library.model.appContext
import com.util.skin.library.observe.SkinObservable
import com.util.skin.library.res.SkinResourcesManager

/**
 * 皮肤管理，使用[SkinConfig]进行初始化设置
 */
object SkinManager : SkinObservable() {
    internal var _config: SkinConfig? = null
    internal val config: SkinConfig
        get() = _config ?: throw IllegalArgumentException("config not init, pls init first")

    /**
     * 恢复默认主题，使用应用自带资源.
     */
    fun restoreDefaultSkin() {
        SkinResourcesManager.resetDefault()
        notifyUpdateSkin()
    }

    /**
     * 加载皮肤
     */
    fun loadSkin() {
        SkinResourcesManager.defaultStrategy()
            ?.apply { loadSkin(this) }
    }

    /**
     * 加载皮肤
     */
    fun loadSkin(
        strategy: SkinStrategy,
        listener: SkinLoaderListener? = null
    ) {
        addSkinLoader(strategy, listener)
        notifyUpdateSkin()
    }

    private fun addSkinLoader(
        strategy: SkinStrategy,
        listener: SkinLoaderListener? = null
    ) {
        listener?.onStart()
        try {
            // 加载资源
            loadTask(strategy)
            listener?.onSuccess()
        } catch (e: Exception) {
            SkinResourcesManager.resetDefault()
            listener?.onFailed("皮肤资源获取失败:$e")
            e.printStackTrace()
        } finally {
            listener?.onFinish()
        }
    }

    private fun loadTask(strategy: SkinStrategy) {
        strategy.loaderStrategy.initStrategy(appContext)?.let {
            SkinResourcesManager.setupResource(strategy)
        } ?: SkinResourcesManager.resetDefault()
    }
}