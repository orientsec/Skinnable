package com.util.skin.library

import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.util.TypedValue
import com.util.skin.library.fallback.ISkinErrorTracker
import com.util.skin.library.fallback.SafeSkinResources
import com.util.skin.library.fallback.SkinErrorCategory
import com.util.skin.library.fallback.SkinErrorEvent
import com.util.skin.library.fallback.SkinFallbackManager
import com.util.skin.library.fallback.SkinTransactionResult
import com.util.skin.library.helpers.SkinHelper
import com.util.skin.library.loader.ResourceLoader
import com.util.skin.library.loader.SkinStrategy
import com.util.skin.library.model.SkinResource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SafeSkinResourcesTest {

    private lateinit var tracker: TestErrorTracker
    private lateinit var fallbackManager: SkinFallbackManager
    private lateinit var safeResources: SafeSkinResources

    @Before
    fun setUp() {
        tracker = TestErrorTracker()
        fallbackManager = SkinFallbackManager(circuitBreakerThreshold = 3, tracker = tracker)
        safeResources = SafeSkinResources(tracker = tracker, fallbackManager = fallbackManager)
    }

    /**
     * 场景 1：模拟外置包缺失某个 R.color 或抛出 NotFoundException 异常，验证 SafeSkinResources 自动代理降级不造成崩溃
     */
    @Test
    fun testExternalSkinMissingColorFallbackToHost() {
        val failingLoader = object : ResourceLoader {
            override fun initStrategy(context: Context): SkinResource? = null
            override fun getColor(context: Context, resId: Int): Int {
                // 模拟外置包中找不到资源 ID 或抛出异常
                throw android.content.res.Resources.NotFoundException("Color ID 0x7f010001 not found in external APK")
            }
            override fun getColorStateList(context: Context, resId: Int): ColorStateList? = null
            override fun getDrawable(context: Context, resId: Int): Drawable? = null
            override fun getXml(context: Context, resId: Int): XmlResourceParser? = null
            override fun getValue(context: Context, resId: Int, outValue: TypedValue, resolveRefs: Boolean) {}
        }

        val fakeContext = object : ContextWrapper(null) {
            override fun getResources(): android.content.res.Resources {
                throw android.content.res.Resources.NotFoundException("Host resource default not found")
            }
        }

        // 调用 getColor，传入 fakeContext（捕获外置包及宿主异常，安全降级返回 INVALID_ID）
        val resultColor = safeResources.getColor(
            context = fakeContext,
            resId = 0x7f010001,
            loader = failingLoader,
            strategyKey = "mock_external_skin"
        )

        // 验证不会向上抛出未捕获异常，并且降级返回 INVALID_ID
        assertEquals(SkinHelper.INVALID_ID, resultColor)

        // 验证错误跟踪器捕获到了资源缺失事件
        assertTrue(tracker.trackedEvents.any { it.category == SkinErrorCategory.MISSING_RESOURCE })
        assertEquals("mock_external_skin", tracker.trackedEvents.last().strategyKey)
    }

    /**
     * 场景 2：验证原子换肤事务 (SkinTransaction) 在失败时的 Rollback 回滚机制
     */
    @Test
    fun testTransactionFailureRollback() {
        val targetStrategy = SkinStrategy.BuildIn("corrupted_skin")
        var rollbackExecuted = false

        val result = fallbackManager.executeTransaction(
            targetStrategy = targetStrategy,
            rollbackAction = {
                rollbackExecuted = true
            },
            block = {
                // 模拟资源加载失败或解压抛出异常
                throw RuntimeException("Corrupted APK file header")
            }
        )

        // 验证返回状态为 FailedRolledBack
        assertTrue(result is SkinTransactionResult.FailedRolledBack)
        assertTrue(rollbackExecuted)

        // 验证触发了 Rollback 埋点上报
        assertTrue(tracker.rollbackEvents.isNotEmpty())
        assertEquals("corrupted_skin", tracker.rollbackEvents.last().first)
    }

    /**
     * 场景 3：验证连续加载失败达到阈值后的熔断锁死机制 (Circuit Breaker)
     */
    @Test
    fun testCircuitBreakerTrippedAfterConsecutiveFailures() {
        val strategy = SkinStrategy.BuildIn("failing_skin")

        // 连续执行 3 次失败事务
        for (i in 1..3) {
            fallbackManager.executeTransaction(
                targetStrategy = strategy,
                rollbackAction = {},
                block = { throw RuntimeException("Simulated load crash $i") }
            )
        }

        // 验证熔断器已开启
        assertTrue(fallbackManager.isCircuitBroken)

        // 第 4 次执行事务，应该直接被熔断拒绝 (CircuitBroken)
        val refusedResult = fallbackManager.executeTransaction(
            targetStrategy = strategy,
            rollbackAction = {},
            block = { true }
        )

        assertTrue(refusedResult is SkinTransactionResult.CircuitBroken)

        // 重置熔断器后恢复正常
        fallbackManager.resetCircuitBreaker()
        assertFalse(fallbackManager.isCircuitBroken)
    }

    private class TestErrorTracker : ISkinErrorTracker {
        val trackedEvents = mutableListOf<SkinErrorEvent>()
        val rollbackEvents = mutableListOf<Pair<String, String>>()

        override fun trackError(event: SkinErrorEvent) {
            trackedEvents.add(event)
        }

        override fun trackRollback(strategyKey: String, reason: String) {
            rollbackEvents.add(strategyKey to reason)
        }
    }
}
