# Skinnable
东方证券Android换肤Kotlin实现方案

# 使用方法
只引用Library
``` 
dependencies {
    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.3.11'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.0.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.0'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'com.github.Orientsec.Skinnable:SkinLibrary:1.0.0'
}
```
添加support仓库 
```
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
