package com.util.skinnable.skinnable.main

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.util.skin.library.SkinManager
import com.util.skin.library.compose.SkinComposeDemoCard
import com.util.skin.library.compose.SkinTheme
import com.util.skin.library.res.SkinUserThemeManager

/**
 * 【View 与 Compose 混排界面无缝换肤集成示例】
 * 演示在传统 AppCompatActivity 中，当点击 View 按钮触发换肤/调色盘切换时，
 * 原生 View 控件与嵌入的 [ComposeView] 同时实现无缝、无闪烁、增量响应式重绘。
 */
class ComposeMixedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose_mixed)

        // 1. 原生 View 按钮交互绑定
        findViewById<Button>(R.id.btnSwitchPink)?.setOnClickListener {
            SkinUserThemeManager.addColorState(R.color.demo_primary, "#E91E63")
            SkinUserThemeManager.addColorState(R.color.demo_surface, "#FCE4EC")
            SkinUserThemeManager.apply()
            Toast.makeText(this, "换肤：切换为动态粉色主题", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnSwitchGreen)?.setOnClickListener {
            SkinUserThemeManager.addColorState(R.color.demo_primary, "#4CAF50")
            SkinUserThemeManager.addColorState(R.color.demo_surface, "#E8F5E9")
            SkinUserThemeManager.apply()
            Toast.makeText(this, "换肤：切换为动态绿色主题", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnReset)?.setOnClickListener {
            SkinUserThemeManager.clearColors()
            SkinManager.restoreDefaultSkin()
            Toast.makeText(this, "恢复默认主题", Toast.LENGTH_SHORT).show()
        }

        // 2. 嵌入式 ComposeView 绑定 SkinTheme 动态主题
        findViewById<ComposeView>(R.id.composeViewInNative)?.setContent {
            SkinTheme(
                colorResourceMapping = mapOf(
                    "primary" to R.color.demo_primary,
                    "surface" to R.color.demo_surface,
                    "textPrimary" to R.color.demo_text
                )
            ) {
                // 嵌套使用的 Compose 组件，自动响应 SkinManager 的 skinStateFlow 重组
                SkinComposeDemoCard(
                    title = "Compose 混合界面卡片",
                    subtitle = "无缝响应 View 层按钮触发的换肤事件！",
                    primaryColorRes = R.color.demo_primary,
                    textColorRes = R.color.demo_text,
                    iconRes = R.drawable.ic_assignment_turned_in_black_24dp
                )
            }
        }
    }
}
