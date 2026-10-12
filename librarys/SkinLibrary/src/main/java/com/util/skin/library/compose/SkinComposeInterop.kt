package com.util.skin.library.compose

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.util.skin.library.SkinManager
import com.util.skin.library.observe.SkinState
import com.util.skin.library.res.SkinResourcesManager

/**
 * 动态 Compose 颜色 Design Token 定义（标记 [@Stable]，性能可控，避免无意义全局重组）。
 */
@Stable
class AppColors(
    primary: Color = Color(0xFF2196F3),
    onPrimary: Color = Color.White,
    background: Color = Color.White,
    onBackground: Color = Color(0xFF212121),
    surface: Color = Color(0xFFF5F5F5),
    onSurface: Color = Color(0xFF212121),
    textPrimary: Color = Color(0xFF212121),
    textSecondary: Color = Color(0xFF757575),
    iconTint: Color = Color(0xFF2196F3),
    isDark: Boolean = false,
    customTokens: Map<String, Color> = emptyMap()
) {
    var primary by mutableStateOf(primary)
        internal set
    var onPrimary by mutableStateOf(onPrimary)
        internal set
    var background by mutableStateOf(background)
        internal set
    var onBackground by mutableStateOf(onBackground)
        internal set
    var surface by mutableStateOf(surface)
        internal set
    var onSurface by mutableStateOf(onSurface)
        internal set
    var textPrimary by mutableStateOf(textPrimary)
        internal set
    var textSecondary by mutableStateOf(textSecondary)
        internal set
    var iconTint by mutableStateOf(iconTint)
        internal set
    var isDark by mutableStateOf(isDark)
        internal set
    var customTokens by mutableStateOf(customTokens)
        internal set

    fun copy(
        primary: Color = this.primary,
        onPrimary: Color = this.onPrimary,
        background: Color = this.background,
        onBackground: Color = this.onBackground,
        surface: Color = this.surface,
        onSurface: Color = this.onSurface,
        textPrimary: Color = this.textPrimary,
        textSecondary: Color = this.textSecondary,
        iconTint: Color = this.iconTint,
        isDark: Boolean = this.isDark,
        customTokens: Map<String, Color> = this.customTokens
    ): AppColors = AppColors(
        primary = primary,
        onPrimary = onPrimary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        iconTint = iconTint,
        isDark = isDark,
        customTokens = customTokens
    )
}

/** 兼容别名 */
typealias SkinColors = AppColors

/**
 * 动态 Compose 字体 Design Token 定义（标记 [@Immutable]）。
 */
@Immutable
class AppTypography(
    val headline: TextStyle = TextStyle.Default,
    val body: TextStyle = TextStyle.Default,
    val button: TextStyle = TextStyle.Default,
    val caption: TextStyle = TextStyle.Default
)

/** 兼容别名 */
typealias SkinTypography = AppTypography

/**
 * 动态 Compose 形状 Design Token 定义（标记 [@Immutable]）。
 */
@Immutable
class AppShapes(
    val small: CornerBasedShape = RoundedCornerShape(4.dp),
    val medium: CornerBasedShape = RoundedCornerShape(8.dp),
    val large: CornerBasedShape = RoundedCornerShape(16.dp)
)

/** 兼容别名 */
typealias SkinShapes = AppShapes

/** 提供给 Compose 视图树访问当前换肤状态 [SkinState] 的 [CompositionLocal] */
val LocalSkinState = staticCompositionLocalOf { SkinState() }

/** 提供给 Compose 视图树访问当前动态颜色 [AppColors] 的 [CompositionLocal] */
val LocalAppColors = staticCompositionLocalOf { AppColors() }

/** 兼容别名 */
val LocalSkinColors = LocalAppColors

/** 提供给 Compose 视图树访问当前字体排版 [AppTypography] 的 [CompositionLocal] */
val LocalAppTypography = staticCompositionLocalOf { AppTypography() }

/** 兼容别名 */
val LocalSkinTypography = LocalAppTypography

/** 提供给 Compose 视图树访问当前形状 [AppShapes] 的 [CompositionLocal] */
val LocalAppShapes = staticCompositionLocalOf { AppShapes() }

/** 兼容别名 */
val LocalSkinShapes = LocalAppShapes

/**
 * [AppTheme] 核心主题属性访问器对象。
 */
object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current

    val shapes: AppShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current

    val skinState: SkinState
        @Composable
        @ReadOnlyComposable
        get() = LocalSkinState.current
}

/** 兼容别名 */
typealias SkinnableTheme = AppTheme

/**
 * 扩展函数：在 Compose 作用域内将宿主 [SkinManager.skinStateFlow] 收集为 Compose [State]。
 */
@Composable
fun SkinManager.collectSkinStateAsState(): State<SkinState> {
    return skinStateFlow.collectAsState()
}

/**
 * Jetpack Compose 与 Skinnable 框架无缝集成的顶层动态 Theme 包装器组件。
 * 自动监听 [SkinManager.skinStateFlow]，并将变动增量推送到 [CompositionLocalProvider]。
 *
 * @param colors 自定义 AppColors 令牌
 * @param typography 自定义 AppTypography 令牌
 * @param shapes 自定义 AppShapes 令牌
 * @param colorResourceMapping 颜色属性与其 R.color ID 的映射字典
 * @param syncMaterial3 是否自动桥接更新 Material 3 [MaterialTheme] 的 ColorScheme
 * @param content 目标 Composable 内容
 */
@Composable
fun SkinTheme(
    colors: AppColors = AppTheme.colors,
    typography: AppTypography = AppTheme.typography,
    shapes: AppShapes = AppTheme.shapes,
    colorResourceMapping: Map<String, Int> = emptyMap(),
    syncMaterial3: Boolean = true,
    content: @Composable () -> Unit
) {
    val skinState by SkinManager.collectSkinStateAsState()
    val context = LocalContext.current

    val updatedColors = remember(skinState, colorResourceMapping) {
        if (colorResourceMapping.isNotEmpty()) {
            val primaryRes = colorResourceMapping["primary"]
            val bgRes = colorResourceMapping["background"]
            val surfaceRes = colorResourceMapping["surface"]
            val textPrimaryRes = colorResourceMapping["textPrimary"]
            val iconTintRes = colorResourceMapping["iconTint"]

            colors.copy(
                primary = primaryRes?.let { Color(SkinResourcesManager.getColor(context, it)) } ?: colors.primary,
                background = bgRes?.let { Color(SkinResourcesManager.getColor(context, it)) } ?: colors.background,
                surface = surfaceRes?.let { Color(SkinResourcesManager.getColor(context, it)) } ?: colors.surface,
                textPrimary = textPrimaryRes?.let { Color(SkinResourcesManager.getColor(context, it)) } ?: colors.textPrimary,
                iconTint = iconTintRes?.let { Color(SkinResourcesManager.getColor(context, it)) } ?: colors.iconTint,
                isDark = !SkinResourcesManager.isDefaultSkin
            )
        } else {
            colors
        }
    }

    CompositionLocalProvider(
        LocalSkinState provides skinState,
        LocalAppColors provides updatedColors,
        LocalAppTypography provides typography,
        LocalAppShapes provides shapes
    ) {
        if (syncMaterial3) {
            val materialColors = if (updatedColors.isDark) {
                darkColorScheme(
                    primary = updatedColors.primary,
                    onPrimary = updatedColors.onPrimary,
                    background = updatedColors.background,
                    onBackground = updatedColors.onBackground,
                    surface = updatedColors.surface,
                    onSurface = updatedColors.onSurface
                )
            } else {
                lightColorScheme(
                    primary = updatedColors.primary,
                    onPrimary = updatedColors.onPrimary,
                    background = updatedColors.background,
                    onBackground = updatedColors.onBackground,
                    surface = updatedColors.surface,
                    onSurface = updatedColors.onSurface
                )
            }
            MaterialTheme(
                colorScheme = materialColors,
                content = content
            )
        } else {
            content()
        }
    }
}

/** 兼容别名 Composable 函数 */
@Composable
fun SkinnableTheme(
    colors: AppColors = AppTheme.colors,
    typography: AppTypography = AppTheme.typography,
    shapes: AppShapes = AppTheme.shapes,
    colorResourceMapping: Map<String, Int> = emptyMap(),
    syncMaterial3: Boolean = true,
    content: @Composable () -> Unit
) {
    SkinTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        colorResourceMapping = colorResourceMapping,
        syncMaterial3 = syncMaterial3,
        content = content
    )
}

/**
 * 动态响应式获取 Compose [Color] 资源。
 *
 * @param id 目标 R.color 资源 ID
 */
@Composable
fun skinColorResource(@ColorRes id: Int): Color {
    val skinState by SkinManager.collectSkinStateAsState()
    val context = LocalContext.current
    return remember(id, skinState) {
        Color(SkinResourcesManager.getColor(context, id))
    }
}

/**
 * 动态响应式获取 Compose [ColorStateList] 资源。
 *
 * @param id 目标 R.color 资源 ID
 */
@Composable
fun skinColorStateListResource(@ColorRes id: Int): ColorStateList? {
    val skinState by SkinManager.collectSkinStateAsState()
    val context = LocalContext.current
    return remember(id, skinState) {
        SkinResourcesManager.getColorStateList(context, id)
    }
}

/**
 * 动态响应式获取 Compose [Painter] 资源（支持 VectorDrawable、BitmapDrawable 及 ColorDrawable）。
 *
 * @param id 目标 R.drawable 资源 ID
 */
@Composable
fun skinPainterResource(@DrawableRes id: Int): Painter? {
    val skinState by SkinManager.collectSkinStateAsState()
    val context = LocalContext.current
    return remember(id, skinState) {
        val drawable = SkinResourcesManager.getDrawable(context, id)
        drawable?.toPainter()
    }
}

private fun Drawable.toPainter(): Painter {
    return when (this) {
        is BitmapDrawable -> {
            bitmap?.let { BitmapPainter(it.asImageBitmap()) } ?: ColorPainter(Color.Transparent)
        }
        else -> {
            val width = if (intrinsicWidth > 0) intrinsicWidth else 1
            val height = if (intrinsicHeight > 0) intrinsicHeight else 1
            try {
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                setBounds(0, 0, canvas.width, canvas.height)
                draw(canvas)
                BitmapPainter(bitmap.asImageBitmap())
            } catch (e: Throwable) {
                ColorPainter(Color.Transparent)
            }
        }
    }
}
