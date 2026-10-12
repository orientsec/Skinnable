package com.util.skin.library.fallback

import com.util.skin.library.loader.SkinStrategy
import com.util.skin.library.utils.SkinPreference
import com.util.skin.library.utils.Slog
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * 换肤事务状态枚举。
 */
enum class SkinTransactionState {
    IDLE,
    EXECUTING,
    COMMITTED,
    ROLLED_BACK
}

/**
 * 换肤原子事务载体。
 */
data class SkinTransaction(
    val targetStrategy: SkinStrategy,
    val previousStrategy: SkinStrategy? = null,
    var state: SkinTransactionState = SkinTransactionState.IDLE,
    val startTime: Long = System.currentTimeMillis()
)

/**
 * 换肤事务执行结果。
 */
sealed class SkinTransactionResult {
    data class Success(val transaction: SkinTransaction) : SkinTransactionResult()
    data class FailedRolledBack(
        val transaction: SkinTransaction,
        val reason: String,
        val cause: Throwable? = null
    ) : SkinTransactionResult()
    data class CircuitBroken(val reason: String) : SkinTransactionResult()
}

/**
 * 【皮肤加载事务与熔断机制 (SkinFallbackManager)】
 * 1. 管理换肤原子事务 (SkinTransaction)。若加载抛出异常、超时或文件校验失败，自动执行 Rollback 回写。
 * 2. 单会话内连续加载失败 N 次后触发熔断机制，自动锁定默认皮肤，清空坏包本地缓存。
 */
class SkinFallbackManager(
    val circuitBreakerThreshold: Int = 3,
    private val tracker: ISkinErrorTracker? = null
) {
    private val consecutiveFailures = AtomicInteger(0)
    private val consecutiveResourceFailures = AtomicInteger(0)
    private val _isCircuitBroken = AtomicBoolean(false)

    /**
     * 熔断器状态：是否已熔断锁定默认皮肤
     */
    val isCircuitBroken: Boolean
        get() = _isCircuitBroken.get()

    /**
     * 上一次验证有效的换肤策略
     */
    var lastValidStrategy: SkinStrategy? = null
        private set

    /**
     * 执行原子换肤事务。若 block 执行抛出异常，自动触发 Rollback 并计算熔断。
     *
     * @param targetStrategy 目标换肤策略
     * @param rollbackAction 发生异常时的回滚闭包 (可重置为 lastValidStrategy 或 默认皮肤)
     * @param block 换肤资源加载逻辑，返回 true 表示初始化验证成功
     */
    fun executeTransaction(
        targetStrategy: SkinStrategy,
        rollbackAction: (previousStrategy: SkinStrategy?) -> Unit,
        block: () -> Boolean
    ): SkinTransactionResult {
        if (isCircuitBroken) {
            val reason = "熔断器已开启（连续失败次数达到 $circuitBreakerThreshold），拒绝执行换肤事务"
            Slog.w("SkinFallbackManager", reason)
            tracker?.trackError(
                SkinErrorEvent(
                    category = SkinErrorCategory.CIRCUIT_BREAKER_TRIPPED,
                    message = reason,
                    strategyKey = targetStrategy.skinName
                )
            )
            return SkinTransactionResult.CircuitBroken(reason)
        }

        val previous = lastValidStrategy
        val transaction = SkinTransaction(
            targetStrategy = targetStrategy,
            previousStrategy = previous,
            state = SkinTransactionState.EXECUTING
        )

        Slog.r("SkinFallbackManager", "Begin SkinTransaction for [${targetStrategy.skinName}]")

        return try {
            val success = block()
            if (success) {
                transaction.state = SkinTransactionState.COMMITTED
                consecutiveFailures.set(0)
                consecutiveResourceFailures.set(0)
                lastValidStrategy = targetStrategy
                Slog.r("SkinFallbackManager", "SkinTransaction COMMITTED for [${targetStrategy.skinName}]")
                SkinTransactionResult.Success(transaction)
            } else {
                val reason = "皮肤包策略初始化失败: [${targetStrategy.skinName}]"
                handleTransactionFailure(transaction, reason, null, rollbackAction)
            }
        } catch (t: Throwable) {
            val reason = "换肤事务捕获异常: ${t.message}"
            handleTransactionFailure(transaction, reason, t, rollbackAction)
        }
    }

    private fun handleTransactionFailure(
        transaction: SkinTransaction,
        reason: String,
        cause: Throwable?,
        rollbackAction: (previousStrategy: SkinStrategy?) -> Unit
    ): SkinTransactionResult.FailedRolledBack {
        transaction.state = SkinTransactionState.ROLLED_BACK
        val failures = consecutiveFailures.incrementAndGet()

        Slog.e("SkinFallbackManager", "SkinTransaction ROLLED_BACK: $reason (Failures count: $failures)", cause)

        // 1. 上报埋点
        tracker?.trackError(
            SkinErrorEvent(
                category = SkinErrorCategory.CORRUPTED_PACKAGE,
                message = reason,
                strategyKey = transaction.targetStrategy.skinName,
                throwable = cause
            )
        )
        tracker?.trackRollback(
            transaction.targetStrategy.skinName,
            "Rollback to previous [${transaction.previousStrategy?.skinName ?: "Default"}] due to: $reason"
        )

        // 2. 执行原子 Rollback
        try {
            rollbackAction(transaction.previousStrategy)
        } catch (t: Throwable) {
            Slog.e("SkinFallbackManager", "Rollback execution failed", t)
        }

        // 3. 检查熔断条件
        if (failures >= circuitBreakerThreshold) {
            tripCircuitBreaker("连续换肤事务失败次数达到阈值 ($failures >= $circuitBreakerThreshold)")
        }

        return SkinTransactionResult.FailedRolledBack(transaction, reason, cause)
    }

    /**
     * 记录资源缺失或查找失败
     */
    fun recordResourceFailure(reason: String) {
        if (isCircuitBroken) return
        val count = consecutiveResourceFailures.incrementAndGet()
        if (count >= circuitBreakerThreshold * 3) {
            tripCircuitBreaker("运行时连续资源查找失败超过阈值 ($count)，熔断锁定默认皮肤")
        }
    }

    /**
     * 记录资源获取成功
     */
    fun recordResourceSuccess() {
        consecutiveResourceFailures.set(0)
    }

    /**
     * 触发熔断
     */
    fun tripCircuitBreaker(reason: String) {
        if (_isCircuitBroken.compareAndSet(false, true)) {
            Slog.w("SkinFallbackManager", "TRIP CIRCUIT BREAKER: $reason")
            SkinPreference.resetResources().commitEditor()
            tracker?.trackError(
                SkinErrorEvent(
                    category = SkinErrorCategory.CIRCUIT_BREAKER_TRIPPED,
                    message = reason
                )
            )
            tracker?.trackRollback("System", "Circuit breaker locked to Default Skin")
        }
    }

    /**
     * 手动重置熔断器（例如用户主动选择全新合法皮肤包或清空缓存后）
     */
    fun resetCircuitBreaker() {
        _isCircuitBroken.set(false)
        consecutiveFailures.set(0)
        consecutiveResourceFailures.set(0)
        Slog.r("SkinFallbackManager", "Circuit breaker reset successfully")
    }
}
