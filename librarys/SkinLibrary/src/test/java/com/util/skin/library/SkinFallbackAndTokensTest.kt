package com.util.skin.library

import com.util.skin.library.compose.SkinColors
import com.util.skin.library.compose.SkinShapes
import com.util.skin.library.compose.SkinTypography
import com.util.skin.library.exception.SkinErrorListener
import com.util.skin.library.exception.SkinErrorType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SkinFallbackAndTokensTest {

    @Test
    fun testSkinErrorListenerAndFallbackReporting() {
        var lastErrorType: SkinErrorType? = null
        var lastErrorMessage: String? = null

        SkinManager._config = com.util.skin.library.model.SkinConfig.builder()
            .setEnableAutoFallback(true)
            .setMaxErrorThreshold(3)
            .setErrorListener(object : SkinErrorListener {
                override fun onError(type: SkinErrorType, message: String, cause: Throwable?) {
                    lastErrorType = type
                    lastErrorMessage = message
                }

                override fun onFallbackToDefault(reason: String) {
                }
            })
            .build()

        SkinManager.reportError(
            SkinErrorType.RESOURCE_NOT_FOUND,
            "Resource test_id not found"
        )

        assertEquals(SkinErrorType.RESOURCE_NOT_FOUND, lastErrorType)
        assertEquals("Resource test_id not found", lastErrorMessage)
    }

    @Test
    fun testSkinColorsTokensCopyAndMutability() {
        val defaultColors = SkinColors()
        assertFalse(defaultColors.isDark)

        val darkColors = defaultColors.copy(isDark = true)
        assertTrue(darkColors.isDark)

        val typography = SkinTypography()
        val shapes = SkinShapes()
        assertNotNull(typography)
        assertNotNull(shapes.small)
    }
}
