package com.util.skin.library

import com.util.skin.library.loader.SkinLoaderListener
import com.util.skin.library.loader.SkinStrategy
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Kotlin 协程挂起版本的换肤方法。
 * 自动将皮肤解压、解析等耗时逻辑切至 [Dispatchers.IO] 线程池异步执行，加载成功后回到主线程分发更新。
 *
 * @param strategy 目标换肤策略（如 [SkinStrategy.BuildIn]、[SkinStrategy.Assets] 等）
 * @return 是否加载成功
 * @throws RuntimeException 如果皮肤解析或文件读取失败，抛出包含错误信息的异常
 */
suspend fun SkinManager.loadSkinSuspend(strategy: SkinStrategy): Boolean {
    return withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            loadSkin(strategy, object : SkinLoaderListener {
                override fun onStart() {}

                override fun onSuccess() {
                    if (continuation.isActive) {
                        continuation.resume(true)
                    }
                }

                override fun onFailed(errMsg: String) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(RuntimeException(errMsg))
                    }
                }

                override fun onFinish() {}
            })
        }
    }
}
