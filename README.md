# Skinnable

东方证券 Android 动态换肤 Kotlin 实现方案。

Skinnable 是一套轻量级、无侵入、扩展性强的 Android 动态换肤框架。支持应用内多主题（内置后缀/前缀）、Assets 插件包换肤、SDCard 本地插件换肤、**用户动态调色盘自定义主题**及 **Jetpack Compose 换肤**，并全面适配 AppCompat、Material Design 及 ConstraintLayout 控件。

---

## 🌟 核心特性

- **零侵入换肤**：只需在 XML 中为控件声明 `app:skinnable="true"` 即可自动完成主题颜色、Drawable、Tint 的实时替换。
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
- **Jetpack Compose 支持 (`SkinnableTheme`)**：通过 `SkinnableTheme` 包装器订阅换肤 `StateFlow`，实现 Compose UI 与原生 View 的统一动态换肤。
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
    implementation("com.github.Orientsec.Skinnable:SkinLibrary:2.1.0")

    // 支持 AppCompat 控件换肤
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("com.github.Orientsec.Skinnable:SkinSupportCompat:2.1.0")

    // 支持 Material Design 控件换肤
    implementation("com.google.android.material:material:1.14.0")
    implementation("com.github.Orientsec.Skinnable:SkinSupportDesign:2.1.0")

    // 支持 ConstraintLayout 换肤
    implementation("androidx.constraintlayout:constraintlayout:2.2.2")
    implementation("com.github.Orientsec.Skinnable:SkinSupportConstraintLayout:2.1.0")
}
```

---

## 🚀 快速上手

### 1. 初始化框架

在 `Application.onCreate()` 中配置换肤 Factory 及其初始化策略：

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SkinConfig.builder()
            .addInflater(SkinAppCompatViewFactory())
            .addInflater(SkinConstraintViewFactory())
            .addInflater(SkinMaterialViewFactory())
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
SkinUserThemeManager.apply() // 保存并全局生效

// 重置自定义主题
SkinUserThemeManager.clearColors()
```

### 3. Jetpack Compose 换肤集成 (`SkinnableTheme`)

为 Compose 布局包裹 `SkinnableTheme` 主题：

```kotlin
@Composable
fun MyComposeScreen() {
    SkinnableTheme {
        val context = LocalContext.current
        val primaryColor = Color(SkinResourcesManager.getColor(context, R.color.demo_primary))
        
        Surface(color = primaryColor) {
            Text(text = "Hello Compose Skinnable!")
        }
    }
}
```

### 4. 带无缝平滑过渡动画的换肤 (`loadSkinWithTransition`)

消除换肤瞬间画面白色闪烁：

```kotlin
SkinManager.loadSkinWithTransition(
    activity = this,
    strategy = SkinStrategy.BuildIn("night"),
    duration = 300L
)
```

### 5. 协程异步换肤 (`loadSkinSuspend`)

结合 Kotlin Coroutines 进行非阻塞的后台加载与 UI 更新：

```kotlin
lifecycleScope.launch {
    val success = SkinManager.loadSkinSuspend(SkinStrategy.BuildIn("night"))
    if (success) {
        // 换肤成功处理
    }
}
```

### 6. 代码 View DSL 绑定 (`bindSkinColor` / `bindSkinCustom`)

针对代码生成的 View：

```kotlin
textView.bindSkinColor(R.color.demo_primary) { color ->
    setTextColor(color)
}

view.bindSkinCustom {
    // 换肤发生时自动重新回调执行
}
```

---

## 🧩 进阶用法

### 1. Fragment 与 Dialog 自动换肤

通过绑定 `SkinLifecycleObserver`，使 Fragment、Dialog 或自定义 `LifecycleOwner` 自动感知换肤并在恢复或显示时重绘：

```kotlin
class TestFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 自动感知的换肤生命周期绑定
        viewLifecycleOwner.lifecycle.addObserver(SkinLifecycleObserver(requireContext()))
    }
}
```

### 2. 自定义 View 支持换肤

对于自定义控件，可以通过实现 `SkinSupportable` 接口并结合辅助类完成换肤适配：

```kotlin
class CustomSkinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), SkinSupportable {

    private val mBackgroundHelper = SkinBackgroundHelper(this)

    override val skinnable: Boolean by lazy {
        SkinResourcesManager.parseSkinnable(context, attrs, defStyleAttr)
    }

    init {
        mBackgroundHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun applySkin() {
        mBackgroundHelper.applySkin()
    }
}
```

---

## 📝 更新日志 (Changelog)

### v2.1.0
- **新增**: 新增 `SkinUserThemeManager` 用户动态调色盘功能，支持无需外部资源包即可实时修改 Theme 颜色与持久化。
- **新增**: 新增 `SkinnableTheme` Jetpack Compose 换肤集成，支持 Compose 组件响应式自动重绘。
- **新增**: 新增 `loadSkinWithTransition` 带淡出遮罩动画的无缝换肤方法，消除界面闪烁。
- **新增**: 新增 `bindSkinColor` / `bindSkinCustom` 等 View 动态 DSL 换肤扩展。
- **新增**: 新增 `loadSkinSuspend` 协程换肤挂起函数，支持非阻塞后台换肤。
- **新增**: 新增 `SkinLifecycleObserver` 自动绑定生命周期感知，在 Fragment/Dialog 重建或显示时自动恢复当前皮肤。
- **优化**: 重构并升级官方 Demo 示例（涵盖 Material Component、Toolbar、CardView、Progress、Compose 及动态调色盘的全要素展示）。
- **修复**: 修复 `SkinCompatLinearLayout` 继承关系导致应用内 ViewBinding 转换引发的 `ClassCastException` 崩溃问题。
