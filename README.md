# Skinnable

东方证券Android换肤Kotlin实现方案

# 使用方法

## 1.引用Library

``` groovy
dependencies {
    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.7.10'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'com.github.Orientsec.Skinnable:SkinLibrary:1.0.0'
}
```

## 2.添加support仓库

``` groovy
dependencies {
    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.7.10'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'com.github.Orientsec.Skinnable:SkinLibrary:1.0.0'

    // 支持appcompat
    implementation 'androidx.appcompat:appcompat:1.5.0'
    implementation 'com.github.Orientsec.Skinnable:SkinSupportCompat:1.0.0'

    // 支持material View
    implementation 'com.google.android.material:material:1.6.1'
    implementation 'com.github.Orientsec.Skinnable:SkinSupportDesign:1.0.0'

    // 支持ConstraintLayout
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.github.Orientsec.Skinnable:SkinSupportConstraintLayout:1.0.0'
}
```

## 在Application.onCreate()方法中加入以下初始化代码

``` kotlin
SkinConfig.builder()
            .addInflater(SkinAppCompatViewFactory())
            .addInflater(SkinConstraintViewFactory())
            .addInflater(SkinMaterialViewFactory())
            .build()
            .initManager(this)
            .loadSkin()
```

在支持换肤的View布局中加入

``` xml
app:skinnable="true"
```

表示支持换肤

# 换肤方法

## 加载插件皮肤库

``` kotlin
// 指定皮肤插件
SkinManager.loadSkin(SkinStrategy.Assets("skin.night"))
// 后缀加载
SkinManager.loadSkin(SkinStrategy.BuildIn("night"))
// 前缀加载
SkinManager.loadSkin(SkinStrategy.PrefixBuildIn("night"))
// 恢复应用默认皮肤
SkinManager.restoreDefaultSkin();
```

## 应用内换肤:

应用内换肤，皮肤名为: night; 新增需要换肤的资源添加后缀或者前缀。

需要换肤的资源为R.color.windowBackgroundColor, 添加对应资源R.color.windowBackgroundColor_night。

加载应用内皮肤:

``` kotlin
// 后缀加载
SkinManager.loadSkin(SkinStrategy.BuildIn("night"))
// 前缀加载
SkinManager.loadSkin(SkinStrategy.PrefixBuildIn("night"))
```

推荐将应用内换肤相关的皮肤资源放到单独的目录中 注: 如果使用这种方式来增加换肤资源，记得在build.gradle 中配置一下这个资源目录

``` kotlin
sourceSets {
    main {
        res.srcDirs = ['src/main/res', 'src/main/res-night']
    }
}
```

# 插件式换肤:

## 新建Android application工程

皮肤工程包名不能和宿主应用包名相同.

## 将需要换肤的资源放到res目录下(同名资源)

例如 APK中窗口背景颜色为 colors.xml

``` xml
<color name="background">#ffffff</color>
```

那么夜间模式你可以在skin-night工程中设置 colors.xml

``` xml
<color name="background">#000000</color>
```

## 打包生成apk, 即为皮肤包

将打包生成的apk文件, 重命名为'xxx.skin', 防止apk结尾的文件造成混淆.

## 加载皮肤插件

加载插件式皮肤, 将皮肤包放到assets/skins目录下

``` kotlin
// 指定皮肤插件
SkinManager.loadSkin(SkinStrategy.Assets("skin.night"))
```