# Skinnable

东方证券 Android 动态换肤 Kotlin 实现方案。

Skinnable 是一套轻量级、无侵入、扩展性强的 Android 动态换肤框架。支持应用内多主题（内置后缀/前缀）、Assets 插件包换肤、SDCard 本地插件换肤，并全面适配 AppCompat、Material Design 及 ConstraintLayout 控件。

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
- **Kotlin 协程支持**：内置 `loadSkinSuspend` 挂起函数，支持后台异步解压/解析皮肤资源，主线程平滑刷新 UI。
- **生命周期感知 (Lifecycle-Aware)**：提供 `SkinLifecycleObserver`，自动监听 Fragment/Dialog 的生命周期并在重建或显示时实时同步皮肤。
- **强大的自定义扩展**：实现 `SkinSupportable` 接口搭配辅助类，轻松赋予自定义 View 换肤能力。

---

## 📦 依赖引入

在项目的 `build.gradle` / `build.gradle.kts` 中添加：

```groovy
dependencies {
    // 核心库
    implementation 'com.github.Orientsec.Skinnable:SkinLibrary:2.1.0'

    // 支持 AppCompat 控件换肤
    implementation 'androidx.appcompat:appcompat:1.8.0'
    implementation 'com.github.Orientsec.Skinnable:SkinSupportCompat:2.1.0'

    // 支持 Material Design 控件换肤
    implementation 'com.google.android.material:material:1.14.0'
    implementation 'com.github.Orientsec.Skinnable:SkinSupportDesign:2.1.0'

    // 支持 ConstraintLayout 换肤
    implementation 'androidx.constraintlayout:constraintlayout:2.2.2'
    implementation 'com.github.Orientsec.Skinnable:SkinSupportConstraintLayout:2.1.0'
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

## 🎨 换肤策略与调用方式

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

### 2. 插件包换肤 (Assets / SDCard 模式)

将皮肤工程（打包生成的独立 APK 或重命名为 `.skin` 的文件）放入 `assets/skins` 目录：

```kotlin
// 指定 Assets 目录插件换肤
SkinManager.loadSkin(SkinStrategy.Assets("skin.night"))

// 指定 SDCard 本地绝对路径插件换肤
SkinManager.loadSkin(SkinStrategy.SDCard("/sdcard/Download/theme.skin"))
```

### 3. 恢复默认主题

```kotlin
SkinManager.restoreDefaultSkin()
```

### 4. 协程异步换肤 (推荐)

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
- **新增**: 新增 `loadSkinSuspend` 协程换肤挂起函数，支持非阻塞后台换肤。
- **新增**: 新增 `SkinLifecycleObserver` 自动绑定生命周期感知，在 Fragment/Dialog 重建或显示时自动恢复当前皮肤。
- **优化**: 重构并升级官方 Demo 示例（涵盖 Material Component、Toolbar、CardView、Progress 等在深色模式下的实时全要素展示）。
- **修复**: 修复 `SkinCompatLinearLayout` 继承关系导致应用内 ViewBinding 转换引发的 `ClassCastException` 崩溃问题。
- **优化**: 内部换肤事件 `notifyUpdateSkin` 在异步线程分发时增加主线程回调检查，提升多线程环境下的稳定性和可靠性。
