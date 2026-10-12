package com.util.skinnable.skinnable.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.util.skin.library.SkinManager
import com.util.skin.library.compose.SkinnableTheme
import com.util.skin.library.loadSkinSuspend
import com.util.skin.library.loader.SkinStrategy
import com.util.skin.library.res.SkinResourcesManager
import com.util.skin.library.res.SkinUserThemeManager
import com.util.skin.library.utils.SkinWebViewHelper
import com.util.skin.library.utils.bindSkinCustom
import com.util.skinnable.skinnable.main.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            // 演示 Feature 1: 用户动态调色盘 (SkinUserThemeManager)
            btnThemePink.setOnClickListener {
                SkinUserThemeManager.addColorState(R.color.demo_primary, "#E91E63")
                SkinUserThemeManager.addColorState(R.color.demo_surface, "#FCE4EC")
                SkinUserThemeManager.apply()
                syncWebViewTheme()
                Toast.makeText(this@MainActivity, "应用动态粉色主题", Toast.LENGTH_SHORT).show()
            }
            btnThemeGreen.setOnClickListener {
                SkinUserThemeManager.addColorState(R.color.demo_primary, "#4CAF50")
                SkinUserThemeManager.addColorState(R.color.demo_surface, "#E8F5E9")
                SkinUserThemeManager.apply()
                syncWebViewTheme()
                Toast.makeText(this@MainActivity, "应用动态绿色主题", Toast.LENGTH_SHORT).show()
            }
            btnThemePurple.setOnClickListener {
                SkinUserThemeManager.addColorState(R.color.demo_primary, "#9C27B0")
                SkinUserThemeManager.addColorState(R.color.demo_surface, "#F3E5F5")
                SkinUserThemeManager.apply()
                syncWebViewTheme()
                Toast.makeText(this@MainActivity, "应用动态紫色主题", Toast.LENGTH_SHORT).show()
            }
            btnThemeReset.setOnClickListener {
                SkinUserThemeManager.clearColors()
                syncWebViewTheme()
                Toast.makeText(this@MainActivity, "已重置自定义主题", Toast.LENGTH_SHORT).show()
            }

            // 演示 Feature 2: 协程异步挂起加载 (loadSkinSuspend)
            btnAsyncSkin.setOnClickListener {
                lifecycleScope.launch {
                    Toast.makeText(this@MainActivity, "开始协程异步加载皮肤...", Toast.LENGTH_SHORT).show()
                    try {
                        val success = SkinManager.loadSkinSuspend(SkinStrategy.BuildIn("night"))
                        if (success) {
                            Toast.makeText(this@MainActivity, "协程异步换肤成功!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "异步换肤失败: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // 演示 Feature 3: Jetpack Compose 与 Skinnable 框架无缝集成 (SkinnableTheme & SkinTokens)
            composeView.setContent {
                SkinnableTheme(
                    colorResourceMapping = mapOf(
                        "primary" to R.color.demo_primary,
                        "surface" to R.color.demo_surface,
                        "textPrimary" to R.color.demo_text
                    )
                ) {
                    val dynamicPrimary = com.util.skin.library.compose.skinColorResource(R.color.demo_primary)
                    val dynamicText = com.util.skin.library.compose.skinColorResource(R.color.demo_text)

                    Surface(
                        color = dynamicPrimary.copy(alpha = 0.15f),
                        shape = SkinnableTheme.shapes.medium,
                        onClick = {
                            startActivity(android.content.Intent(this@MainActivity, ComposeMixedActivity::class.java))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Compose Composable 视图组件 (点击打开混排 Demo 页面)",
                                color = dynamicText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "订阅 Skinnable StateFlow & SkinTokens，换肤/调色时自动局部重绘!",
                                color = dynamicPrimary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // 演示 Feature 6: WebView / H5 动态主题注入 (SkinWebViewHelper)
            webView.settings.javaScriptEnabled = true
            val html = """
                <!DOCTYPE html>
                <html>
                <head>
                <style>
                  :root {
                    --skin-bg-color: #ffffff;
                    --skin-text-color: #333333;
                  }
                  html.skin-dark, body.skin-dark {
                    --skin-bg-color: #1e1e1e;
                    --skin-text-color: #e0e0e0;
                  }
                  html.skin-light, body.skin-light {
                    --skin-bg-color: #ffffff;
                    --skin-text-color: #333333;
                  }
                  body {
                    font-family: sans-serif;
                    margin: 4px;
                    padding: 8px;
                    background-color: var(--skin-bg-color, #ffffff);
                    color: var(--skin-text-color, #333333);
                    border-radius: 6px;
                    transition: all 0.3s ease;
                  }
                  .badge {
                    display: inline-block;
                    padding: 3px 6px;
                    background-color: var(--skin-primary-color, #2196F3);
                    color: white;
                    border-radius: 4px;
                    font-size: 11px;
                    font-weight: bold;
                  }
                </style>
                </head>
                <body class="skin-light">
                  <div>H5 Web Container <span class="badge">Dynamic CSS</span></div>
                  <p style="font-size: 11px; margin-top: 4px;">Receives JS injection from SkinWebViewHelper.</p>
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                    syncWebViewTheme()
                }
            }

            lifecycleScope.launch {
                SkinManager.skinStateFlow.collect {
                    syncWebViewTheme()
                }
            }

            // 演示 Feature 4: 代码 View DSL 换肤绑定 (bindSkinCustom / bindSkinColor)
            tvText1.bindSkinCustom {
                setOnClickListener {
                    SkinManager.restoreDefaultSkin()
                }
            }
            tvText2.setOnClickListener {
                // 演示 Feature 5: 带无缝平滑淡出动画的换肤
                SkinManager.loadSkinWithTransition(
                    activity = this@MainActivity,
                    strategy = SkinStrategy.BuildIn("night"),
                    duration = 350L
                )
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab!!.position) {
                        0 -> {
                            SkinManager.restoreDefaultSkin()
                        }
                        1 -> {
                            SkinManager.loadSkinWithTransition(
                                activity = this@MainActivity,
                                strategy = SkinStrategy.BuildIn("night"),
                                duration = 300L
                            )
                        }
                        2 -> {
                            SkinManager.loadSkinWithTransition(
                                activity = this@MainActivity,
                                strategy = SkinStrategy.PrefixBuildIn("night"),
                                duration = 300L
                            )
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            // 动态添加 ParentViewPagerFragment (包含 ViewPager2 + 嵌套 ChildFragment + RecyclerView + DSL)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ParentViewPagerFragment())
                .commit()
        }
    }

    private fun syncWebViewTheme() {
        val isDark = !SkinResourcesManager.isDefaultSkin
        val primaryColorInt = SkinResourcesManager.getColor(this, R.color.demo_primary)
        val surfaceColorInt = SkinResourcesManager.getColor(this, R.color.demo_surface)
        val textColorInt = SkinResourcesManager.getColor(this, R.color.demo_text)

        val primaryHex = String.format("#%06X", 0xFFFFFF and primaryColorInt)
        val surfaceHex = String.format("#%06X", 0xFFFFFF and surfaceColorInt)
        val textHex = String.format("#%06X", 0xFFFFFF and textColorInt)

        _binding?.webView?.let {
            SkinWebViewHelper.syncThemeToWebView(
                it,
                isDark = isDark,
                themeColorHex = primaryHex,
                bgColorHex = surfaceHex,
                textColorHex = textHex
            )
        }
    }
}