package com.util.skin.library.utils

import android.webkit.WebView

/**
 * 【架构意图】：WebView 动态主题与深色模式/CSS 变量注入助手。
 * 支持同步 App 换肤状态至 H5 页面，通过 JS 评估机制自动为 <html> 和 <body> 标签注入 skin-dark/skin-light 样式类名及自定义 CSS 变量 (--skin-primary-color, --skin-bg-color, --skin-text-color)。
 *
 * 【潜在兼容风险】：依赖系统 WebView 组件及其 Javascript 运行机制，要求 WebView 必须已启用 `settings.javaScriptEnabled = true`。
 * 【验证方式】：调用 `syncThemeToWebView` 后，在 H5 页面检视 `document.documentElement.className` 及 `--skin-bg-color` / `--skin-text-color` 属性是否正确变更。
 */
object SkinWebViewHelper {

    /**
     * 将当前的换肤与主题状态（例如深色/浅色模式、主题色、背景色、文字色）同步至 [WebView]。
     *
     * @param webView 目标 WebView 控件
     * @param isDark 当前是否为深色模式/夜间皮肤
     * @param themeColorHex 当前主题色（十六进制字符串，如 "#2196F3"）
     * @param bgColorHex 当前页面背景色（十六进制字符串，如 "#FFFFFF"）
     * @param textColorHex 当前文字颜色（十六进制字符串，如 "#333333"）
     */
    fun syncThemeToWebView(
        webView: WebView,
        isDark: Boolean = false,
        themeColorHex: String = "#2196F3",
        bgColorHex: String? = null,
        textColorHex: String? = null
    ) {
        val themeClass = if (isDark) "skin-dark" else "skin-light"
        val bgSet = if (bgColorHex != null) "document.documentElement.style.setProperty('--skin-bg-color', '$bgColorHex');" else ""
        val textSet = if (textColorHex != null) "document.documentElement.style.setProperty('--skin-text-color', '$textColorHex');" else ""

        val js = """
            (function() {
                try {
                    document.documentElement.classList.remove('skin-dark', 'skin-light');
                    document.documentElement.classList.add('$themeClass');
                    if (document.body) {
                        document.body.classList.remove('skin-dark', 'skin-light');
                        document.body.classList.add('$themeClass');
                    }
                    document.documentElement.style.setProperty('--skin-primary-color', '$themeColorHex');
                    $bgSet
                    $textSet
                } catch(e) {}
            })();
        """.trimIndent()

        webView.post {
            try {
                webView.evaluateJavascript(js, null)
            } catch (e: Exception) {
                Slog.e("SkinWebViewHelper", "evaluateJavascript failed", e)
            }
        }
    }
}
