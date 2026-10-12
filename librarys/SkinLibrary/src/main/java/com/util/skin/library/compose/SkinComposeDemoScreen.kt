package com.util.skin.library.compose

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 示例 Composable 组件：演示动态 Design Token、动态 Color 与动态 Painter (图标) 切换。
 */
@Composable
fun SkinComposeDemoCard(
    title: String,
    subtitle: String,
    @ColorRes primaryColorRes: Int,
    @ColorRes textColorRes: Int,
    @DrawableRes iconRes: Int? = null,
    modifier: Modifier = Modifier
) {
    // 动态订阅获取皮肤资源（当 View 框架执行换肤时，仅当前 Composable 响应式局部重组）
    val dynamicPrimary = skinColorResource(primaryColorRes)
    val dynamicText = skinColorResource(textColorRes)
    val dynamicPainter = iconRes?.let { skinPainterResource(it) }

    Card(
        shape = AppTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = dynamicPrimary.copy(alpha = 0.12f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            dynamicPainter?.let { painter ->
                Image(
                    painter = painter,
                    contentDescription = "Dynamic Icon",
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = dynamicText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = dynamicPrimary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
