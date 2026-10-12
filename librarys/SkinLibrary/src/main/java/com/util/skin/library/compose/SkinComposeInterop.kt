package com.util.skin.library.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import com.util.skin.library.SkinManager
import com.util.skin.library.observe.SkinState

/**
 * 提供给 Compose 视图层次结构访问当前换肤状态 [SkinState] 的 [CompositionLocal]。
 */
val LocalSkinState = staticCompositionLocalOf { SkinState() }

/**
 * Jetpack Compose 与 Skinnable 框架无缝集成的 Composable 主题包装器。
 * 内部自动订阅 [SkinManager.skinStateFlow]。当应用发生换肤或用户自定义主题更新时，
 * 该包装器内部的状态更新将自动触发包裹的 Compose UI 内容重建与资源重算。
 *
 * @param content 需要响应动态换肤的 Compose 视图内容
 */
@Composable
fun SkinnableTheme(
    content: @Composable () -> Unit
) {
    val skinState = SkinManager.skinStateFlow.collectAsState().value
    CompositionLocalProvider(
        LocalSkinState provides skinState,
        content = content
    )
}