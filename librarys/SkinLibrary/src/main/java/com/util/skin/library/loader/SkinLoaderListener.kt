package com.util.skin.library.loader

/**
 * 皮肤包加载监听.
 * @PackageName com.util.skin.library.loader
 * @date 2019/1/10 14:37
 * @author zhanglei
 */
interface SkinLoaderListener {
    /**
     * 开始加载.
     */
    fun onStart()

    /**
     * 加载成功.
     */
    fun onSuccess()

    /**
     * 加载失败.
     *
     * @param errMsg 错误信息.
     */
    fun onFailed(errMsg: String)

    fun onFinish()
}