# Skinnable
东方证券Android换肤Kotlin实现方案

# 使用方法
## 1.引用Library
``` 
dependencies {
    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.3.11'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.0.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.0'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'com.github.Orientsec.Skinnable:SkinLibrary:1.0.0'
}
```
## 2.添加support仓库 
``` groovy
dependencies {
    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.3.11'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.0.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.0'
    implementation 'com.github.Orientsec.Skinnable:SkinLibrary:1.0.0'

    // 支持appcompat
    implementation 'androidx.appcompat:appcompat:1.0.2'
    implementation 'com.github.Orientsec.Skinnable:SkinSupportCompat:1.0.0'

    // 支持material View
    implementation 'com.google.android.material:material:1.1.0-alpha02'
    implementation 'com.github.Orientsec.Skinnable:SkinSupportDesign:1.0.0'

    // 支持ConstraintLayout
    implementation 'androidx.constraintlayout:constraintlayout:2.0.0-alpha3'
    implementation 'com.github.Orientsec.Skinnable:SkinSupportConstraintLayout:1.0.0'
}
```
## 在Application.onCreate()方法中加入以下初始化代码
``` kotlin
SkinManager.init(this)
           .addInflater(SkinAppCompatViewInflater())
           .addInflater(SkinConstraintViewInflater())
           .addInflater(SkinMaterialViewInflater())
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
SkinManager.loadSkin("skin.night", SkinLoaderStrategyType.Assets)

// 恢复应用默认皮肤
SkinManager.restoreDefaultTheme();
```
## 应用内换肤:
应用内换肤，皮肤名为: night; 新增需要换肤的资源添加后缀或者前缀。

需要换肤的资源为R.color.windowBackgroundColor, 添加对应资源R.color.windowBackgroundColor_night。

加载应用内皮肤:
``` kotlin
// 后缀加载
SkinManager.loadSkin("night", SkinLoaderStrategyType.BuildIn);
// 前缀加载
SkinManager.loadSkin("night", SkinLoaderStrategyType.PrefixBuildIn); 
```
推荐将应用内换肤相关的皮肤资源放到单独的目录中
注: 如果使用这种方式来增加换肤资源，记得在build.gradle 中配置一下这个资源目录 
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
例如 APK中窗口背景颜色为
colors.xml
```
<color name="background">#ffffff</color>
```
那么夜间模式你可以在skin-night工程中设置
colors.xml
```
<color name="background">#000000</c
```
## 打包生成apk, 即为皮肤包
将打包生成的apk文件, 重命名为'xxx.skin', 防止apk结尾的文件造成混淆.

## 加载皮肤插件
加载插件式皮肤, 将皮肤包放到assets/skins目录下
``` kotlin
// 指定皮肤插件
SkinManager.loadSkin("skin.night", SkinLoaderStrategyType.Assets)
```
# 自定义加载策略:
## 自定义sdcard路径
继承自SkinSDCardLoader，通过getSkinPath方法指定皮肤加载路径，通过getType方法指定加载器type。
```
public class CustomSDCardLoader extends SkinSDCardLoader {
    @Override
    protected String getSkinPath(Context context, String skinName) {
        return new File(SkinFileUtils.getSkinDir(context), skinName).getAbsolutePath();
    }

    @Override
    public SkinLoaderStrategyType getType() {
        return SkinLoaderStrategyType.SDCard;
    }
}
```
注: 自定义加载器type 值最好从整数最大值开始递减，框架的type值从小数开始递增，以免将来框架升级造成type 值冲突

## 在Application中，添加自定义加载策略:
```
SkinManager.addStrategy(CustomSDCardLoader());          // 自定义加载策略，指定SDCard路径
```
注: 自定义加载器必须在Application中注册，皮肤切换后，重启应用需要根据当前策略加载皮肤

使用自定义加载器加载皮肤:
```
SkinManager.loadSkin("night.skin", null, SkinLoaderStrategyType.SDCard);
```
## zip包中加载资源
继承自SkinSDCardLoader，在loadSkinInBackground方法中解压资源，在getDrawable等方法中返回加压后的资源。
```
public class ZipSDCardLoader extends SkinSDCardLoader {

    @Override
    public String loadSkinInBackground(Context context, String skinName) {
        // TODO 解压zip包中的资源，同时可以根据skinName安装皮肤包(.skin)。
        return super.loadSkinInBackground(context, skinName);
    }

    @Override
    protected String getSkinPath(Context context, String skinName) {
        // TODO 返回皮肤包路径，如果自需要使用zip包，则返回""
        return new File(SkinFileUtils.getSkinDir(context), skinName).getAbsolutePath();
    }

    @Override
    public Drawable getDrawable(Context context, String skinName, int resId) {
        // TODO 根据resId来判断是否使用zip包中的资源。
        return super.getDrawable(context, skinName, resId);
    }

    @Override
    public SkinLoaderStrategyType getType() {
        return SkinLoaderStrategyType.Zip;
    }
}
```
资源加载策略更灵活，不仅仅只有皮肤包，开发者可配置任意资源获取方式(Zip/Apk/Json...)。

在Application中，添加自定义加载策略:
```
SkinManager.addStrategy(ZipSDCardLoader());          // 自定义加载策略，加载zip包中的资源
```