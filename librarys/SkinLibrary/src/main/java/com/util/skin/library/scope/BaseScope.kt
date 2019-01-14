package com.util.skin.library.scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * @PackageName com.util.skin.library.scope
 * @date 2019/1/10 13:53
 * @author zhanglei
 */
internal class BaseScope : CoroutineScope {
    private lateinit var job: Job

    fun create() {
        job = Job()
    }

    fun destroy() {
        job.cancel()
    }
    // to be continued ...

    // class Activity continues
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
}