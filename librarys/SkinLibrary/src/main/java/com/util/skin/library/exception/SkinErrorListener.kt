package com.util.skin.library.exception

/**
 * 换肤过程中发生的异常错误类型枚举。
 */
enum class SkinErrorType {
    /**
     * 皮肤包下载、解压、卡锁或策略加载失败
     */
    LOAD_FAILED,

    /**
     * 换肤资源（Color/Drawable/Xml/Value等）在皮肤包中未找到
     */
    RESOURCE_NOT_FOUND,

    /**
     * 图片解码失败或内存溢出 (OOM)
     */
    DECODE_OR_OOM,

    /**
     * 其他非预期未知异常
     */
    UNEXPECTED
}

/**
 * 全局换肤异常监控与降级兜底回调接口。
 */
interface SkinErrorListener {
    /**
     * 当发生换肤相关异常时回调（如资源未找到、解码失败、加载失败等）。
     *
     * @param type 异常类型 [SkinErrorType]
     * @param message 异常描述信息
     * @param cause 原始异常对象（可能为 null）
     */
    fun onError(type: SkinErrorType, message: String, cause: Throwable?)

    /**
     * 当连续换肤异常次数达到设定阈值，或者皮肤包彻底损坏触发降级回退到默认主题时回调。
     *
     * @param reason 降级回退的原因说明
     */
    fun onFallbackToDefault(reason: String)
}
