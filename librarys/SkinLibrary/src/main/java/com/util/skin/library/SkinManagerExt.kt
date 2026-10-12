package com.util.skin.library

import com.util.skin.library.loader.SkinLoaderListener
import com.util.skin.library.loader.SkinStrategy
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 协程版本的皮肤加载，自动切换到 IO 线程池加载并在主线程应用
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
