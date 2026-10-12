package com.util.skin.library.fallback

/**
 * 换肤异常分类枚举定义。
 */
enum class SkinErrorCategory {
    /**
     * 资源缺失（如外置皮肤包中找不到指定的 R.color 或 R.drawable ID / entryName）
     */
    MISSING_RESOURCE,

    /**
     * 图片/矢量图解码溢出 (OOM) 或 BitMap 绘制失败
     */
    OOM_DECODE,

    /**
     * 皮肤包损坏、解压失败或文件签名/哈希校验未通过
     */
    CORRUPTED_PACKAGE,

    /**
     * 换肤事务加载超时（如网络下载超时或大图片资源解压耗时过长）
     */
    TIMEOUT,

    /**
     * 触发事务 Rollback 回滚到上一有效皮肤或默认皮肤
     */
    ROLLBACK_TRIGGERED,

    /**
     * 熔断机制触发（连续失败次数达到阈值后锁定默认皮肤并清除损坏缓存）
     */
    CIRCUIT_BREAKER_TRIPPED
}

/**
 * 换肤异常事件数据载体类。
 */
data class SkinErrorEvent(
    val category: SkinErrorCategory,
    val message: String,
    val resId: Int = 0,
    val resName: String = "",
    val strategyKey: String = "",
    val throwable: Throwable? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 换肤异常监控与告警埋点契约接口。
 * 供 APM 平台或日志告警框架注册使用。
 */
interface ISkinErrorTracker {
    /**
     * 上报换肤过程中的异常事件。
     */
    fun trackError(event: SkinErrorEvent)

    /**
     * 上报事务回滚与降级事件。
     */
    fun trackRollback(strategyKey: String, reason: String)
}
