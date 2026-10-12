# Skinnable

东方证券 Android 动态换肤 Kotlin 实现方案。

Skinnable 是一套轻量级、无侵入、高可用、生产级的 Android 动态换肤框架。支持应用内多主题（内置后缀/前缀）、Assets 插件包换肤、SDCard 本地插件换肤、**用户动态调色盘自定义主题**及 **Jetpack Compose 深度无缝换肤**，并全面适配 AppCompat、Material Design 及 ConstraintLayout 控件。

内部内置 **SafeSkinResources 三级安全资源代理** 与 **SkinFallbackManager 事务熔断兜底引擎**，保证在大规模商业级应用中绝不因皮肤包坏包、缺失 ID 或 OOM 解码失败而导致崩溃或黑屏。

---

## 🌟 核心特性

- **零侵入换肤**：只需在 XML 中为控件声明 `app:skinnable="true"` 即可自动完成主题颜色、Drawable、Tint 的实时替换。
- **三级安全代理 (SafeSkinResources)**：代理 Layer 1 (用户调色盘) -> Layer 2 (外置/内置皮肤包) -> Layer 3 (宿主原生默认资源)。捕获一切外置包解析异常、ID 映射丢失、图片 OOM 解码失败等，严禁向上抛出未捕获异常。
- **事务与熔断机制 (SkinFallbackManager)**：换肤切换基于 `SkinTransaction` 原子事务，失败自动 Rollback 回退；单会话连续加载失败达阈值后自动触发**熔断锁死**，恢复默认主题并清除损坏缓存。
- **异常监控与 APM 埋点 (ISkinErrorTracker)**：抽象 `ISkinErrorTracker` 契约，支持捕获上报 `MISSING_RESOURCE`、`OOM_DECODE`、`CORRUPTED_PACKAGE`、`TIMEOUT`、`ROLLBACK_TRIGGERED` 及 `CIRCUIT_BREAKER_TRIPPED` 事件。
- **Jetpack Compose 深度换肤 (`SkinTheme`)**：
  - **Design Tokens 体系**：基于 `@Stable` 的 `AppColors` 和 `@Immutable` 的 `AppTypography` / `AppShapes` 令牌。
  - **增量局部重组**：通过 `collectSkinStateAsState()` 订阅换肤状态流，配合 `skinColorResource` 与 `skinPainterResource` 实现局部高性能重组。
  - **Material 3 自动映射**：自动将 `AppColors` 同步桥接至 Compose Material 3 `MaterialTheme` (`darkColorScheme` / `lightColorScheme`)。
- **丰富的控件支持**：
  - **SkinSupportCompat**：支持原生 View、`TextView`、`ImageView`、`Button`、`EditText`、`SeekBar`、`ProgressBar`、`Toolbar` 等。
  - **SkinSupportDesign**：支持 `MaterialCardView`、`FloatingActionButton`、`TabLayout`、`AppBarLayout`、`BottomNavigationView`、`TextInputLayout` 等。
  - **SkinSupportConstraintLayout**：支持 `ConstraintLayout` 换肤。
- **灵活的换肤策略 (SkinStrategy)**：
  - `BuildIn("night")`：应用内后缀资源换肤（如 `color_primary_night`）。
  - `PrefixBuildIn("night")`：应用内前缀资源换肤（如 `night_color_primary`）。
  - `Assets("skin.night")`：Assets 目录插件包换肤。
  - `SDCard("/sdcard/skins/skin.night")`：SD 卡本地插件包换肤。
- **用户动态调色盘 (`SkinUserThemeManager`)**：支持在运行时自由修改颜色与图片（无需制作/加载皮肤 APK 资源包），配置自动持久化保存。
- **无缝平滑淡出动画 (`loadSkinWithTransition`)**：自动捕捉窗口画面遮罩并在后台切皮肤，渐变淡出遮罩消除画面白色闪烁。
- **Kotlin 协程支持 (`loadSkinSuspend`)**：内置 `loadSkinSuspend` 挂起函数，支持后台异步解压/解析皮肤资源，主线程平滑刷新 UI。
- **代码 View DSL 绑定 (`bindSkinColor` / `bindSkinCustom`)**：针对代码动态创建的 View，支持链式 DSL 动态绑定换肤逻辑。
- **生命周期感知 (Lifecycle-Aware)**：提供 `SkinLifecycleObserver`，自动监听 Fragment/Dialog 的生命周期并在重绘或恢复时实时同步皮肤。

---

## 📦 依赖引入

在项目的 `build.gradle` / `build.gradle.kts` 中添加：

```kotlin
dependencies {
    // 核心库
    implementation("com.github.Orientsec.Skinnable:SkinLibrary:2.2.0")

    // 支持 AppCompat 控件换肤
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("com.github.Orientsec.Skinnable:SkinSupportCompat:2.2.0")

    // 支持 Material Design 控件换肤
    implementation("com.google.android.material:material:1.14.0")
    implementation("com.github.Orientsec.Skinnable:SkinSupportDesign:2.2.0")

    // 支持 ConstraintLayout 换肤
    implementation("androidx.constraintlayout:constraintlayout:2.2.2")
    implementation("com.github.Orientsec.Skinnable:SkinSupportConstraintLayout:2.2.2")
}
```

---

## 🚀 快速上手

### 1. 初始化框架与异常监控配置

在 `Application.onCreate()` 中配置换肤 Factory、异常监控与熔断阈值：

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SkinConfig.builder()
            .addInflater(SkinAppCompatViewFactory())
            .addInflater(SkinConstraintViewFactory())
            .addInflater(SkinMaterialViewFactory())
            .setEnableAutoFallback(true) // 开启异常自动降级
            .setMaxErrorThreshold(5)     // 连续 5 次加载/缺失异常自动触发熔断降级
            .setErrorListener(object : SkinErrorListener {
                override fun onError(type: SkinErrorType, message: String, cause: Throwable?) {
                    Log.e("SkinMonitor", "Skin Exception [$type]: $message", cause)
                }

                override fun onFallbackToDefault(reason: String) {
                    Toast.makeText(this@App, "换肤降级: $reason", Toast.LENGTH_LONG).show()
                }
            })
            .build()
            .initManager(this)
            .loadSkin() // 加载上次保存的皮肤状态
    }
}
```

### 2. 在 XML 中声明换肤属性

在需要换肤的控件上加上 `app:skinnable="true"` 属性：

```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@color/bg_color"
    android:textColor="@color/text_color"
    app:skinnable="true" />
```

---

## 🎨 换肤策略与高级调用

### 1. 应用内换肤 (后缀 / 前缀模式)

推荐将应用内换肤资源放到单独的源码目录（例如 `src/main/res-night`），并在 `build.gradle.kts` 中配置：

```kotlin
android {
    sourceSets {
        getByName("main") {
            res.srcDirs("src/main/res", "src/main/res-night")
        }
    }
}
```

- **后缀加载**：将夜间模式资源命名为 `bg_color_night`，调用：
  ```kotlin
  SkinManager.loadSkin(SkinStrategy.BuildIn("night"))
  ```
- **前缀加载**：将夜间模式资源命名为 `night_bg_color`，调用：
  ```kotlin
  SkinManager.loadSkin(SkinStrategy.PrefixBuildIn("night"))
  ```

### 2. 用户动态调色盘自定义主题 (`SkinUserThemeManager`)

无需打包加载皮肤 APK，直接在代码中修改指定颜色资源或图片路径：

```kotlin
// 设置品牌主色为粉色
SkinUserThemeManager.addColorState(R.color.demo_primary, "#E91E63")
SkinUserThemeManager.apply() // 保存并全局生效（支持与 Compose 实时同步变色）

// 重置自定义主题
SkinUserThemeManager.clearColors()
```

### 3. Jetpack Compose 深度无缝换肤 (`SkinTheme` & `AppTheme`)

在 Compose 界面中使用 `SkinTheme` 包装器，结合 `AppTheme` 动态 Design Tokens 和 `skinColorResource`：

```kotlin
@Composable
fun MyComposeScreen() {
    SkinTheme(
        colorResourceMapping = mapOf(
            "primary" to R.color.demo_primary,
            "surface" to R.color.demo_surface,
            "textPrimary" to R.color.demo_text
        )
    ) {
        // 使用 AppTheme 响应式 Design Tokens
        val primaryColor = AppTheme.colors.primary
        val dynamicIcon = skinPainterResource(R.drawable.ic_logo)

        Surface(color = primaryColor) {
            Row {
                dynamicIcon?.let { Image(painter = it, contentDescription = null) }
                Text(text = "Hello Compose Skinnable!", color = AppTheme.colors.textPrimary)
            }
        }
    }
}
```

### 4. APM 告警埋点契约接入 (`ISkinErrorTracker`)

绑定全局 APM 异常埋点采集契约：

```kotlin
SkinManager.errorTracker = object : ISkinErrorTracker {
    override fun trackError(event: SkinErrorEvent) {
        APM.reportEvent("skin_error", mapOf(
            "category" to event.category.name,
            "message" to event.message,
            "resId" to event.resId,
            "resName" to event.resName,
            "strategyKey" to event.strategyKey
        ))
    }

    override fun trackRollback(strategyKey: String, reason: String) {
        APM.reportEvent("skin_rollback", mapOf(
            "strategyKey" to strategyKey,
            "reason" to reason
        ))
    }
}
```

### 5. 带无缝平滑过渡动画的换肤 (`loadSkinWithTransition`)

消除换肤瞬间画面白色闪烁：

```kotlin
SkinManager.loadSkinWithTransition(
    activity = this,
    strategy = SkinStrategy.BuildIn("night"),
    duration = 300L
)
```

### 6. 协程异步换肤 (`loadSkinSuspend`)

结合 Kotlin Coroutines 进行非阻塞的后台加载与 UI 更新：

```kotlin
lifecycleScope.launch {
    val success = SkinManager.loadSkinSuspend(SkinStrategy.BuildIn("night"))
    if (success) {
        // 换肤成功处理
    }
}
```

---

## 🧩 进阶用法与混排支持

### 1. View + Compose 混排界面无缝换肤 (`ComposeMixedActivity`)

在包含传统 Android View 与嵌合 `ComposeView` 的传统 Activity 中，点击 View 层控件发起换肤或动态调色盘切换，原生 View 与 `ComposeView` 均可增量实时无闪烁重绘：

```kotlin
class ComposeMixedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose_mixed)

        // 原生 View 按钮交互触发换肤
        findViewById<Button>(R.id.btnSwitchPink)?.setOnClickListener {
            SkinUserThemeManager.addColorState(R.color.demo_primary, "#E91E63")
            SkinUserThemeManager.apply() // 原生 View 与 Compose 实时秒级变色
        }

        // 嵌合 ComposeView 绑定
        findViewById<ComposeView>(R.id.composeViewInNative)?.setContent {
            SkinTheme {
                SkinComposeDemoCard(
                    title = "Compose 卡片组件",
                    subtitle = "实时响应 View 层换肤事件！",
                    primaryColorRes = R.color.demo_primary,
                    textColorRes = R.color.demo_text
                )
            }
        }
    }
}
```

### 2. Fragment 与 Dialog 自动换肤

通过绑定 `SkinLifecycleObserver`，使 Fragment、Dialog 或自定义 `LifecycleOwner` 自动感知换肤并在恢复或显示时重绘：

```kotlin
class TestFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(SkinLifecycleObserver(requireContext()))
    }
}
```

---

## 📝 更新日志 (Changelog)

### v2.2.0 (生产级安全兜底与 Compose 重构升级)
- **新增**: 重构推出 **SafeSkinResources 三级代理** (Layer 1 调色盘 -> Layer 2 插件包 -> Layer 3 宿主默认资源)，捕获一切外置包解析异常、缺失 ID、OOM 等，严禁向 View 层抛出未捕获崩溃。
- **新增**: 新增 **SkinFallbackManager 事务与熔断器**，实现 `SkinTransaction` 原子换肤事务 Rollback 回滚与单会话连续失败熔断锁死，自动清空损坏缓存并锁定默认主题。
- **新增**: 新增 **ISkinErrorTracker APM 告警埋点契约**，支持捕获上报 `MISSING_RESOURCE`、`OOM_DECODE`、`CORRUPTED_PACKAGE`、`TIMEOUT`、`ROLLBACK_TRIGGERED` 与 `CIRCUIT_BREAKER_TRIPPED` 事件。
- **重构**: 重构升华 **Jetpack Compose 换肤体系**，推出 `@Stable` / `@Immutable` 限制的 `AppColors` / `AppTypography` / `AppShapes` 动态 Tokens、`SkinTheme` 主题包装器及 `skinColorResource` / `skinPainterResource` 增量重组函数，支持无缝映射 Compose Material 3 `MaterialTheme`。
- **新增**: 新增 `ComposeMixedActivity` 传统 View 与 ComposeView 混排全场景无缝换肤演示范例。
- **修复**: 修复系统框架资源 (`0x01xxxxxx`) 以及皮肤包正常未覆盖资源被误判定为熔断失败计数的 Bug。
- **修复**: 修复 `SkinUserThemeManager.apply()` 调色盘刷新时未能成功发射 `_skinStateFlow` 导致 Compose 界面未同步变色的 Bug。

### v2.1.0
- **新增**: 新增 `SkinUserThemeManager` 用户动态调色盘功能，支持无需外部资源包即可实时修改 Theme 颜色与持久化。
- **新增**: 新增 `SkinnableTheme` Jetpack Compose 换肤集成，支持 Compose 组件响应式自动重绘。
- **新增**: 新增 `loadSkinWithTransition` 带淡出遮罩动画的无缝换肤方法，消除界面闪烁。
- **新增**: 新增 `bindSkinColor` / `bindSkinCustom` 等 View 动态 DSL 换肤扩展。
- **新增**: 新增 `loadSkinSuspend` 协程换肤挂起函数，支持非阻塞后台换肤。
- **新增**: 新增 `SkinLifecycleObserver` 自动绑定生命周期感知，在 Fragment/Dialog 重建或显示时自动恢复当前皮肤。
- **优化**: 重构并升级官方 Demo 示例。
