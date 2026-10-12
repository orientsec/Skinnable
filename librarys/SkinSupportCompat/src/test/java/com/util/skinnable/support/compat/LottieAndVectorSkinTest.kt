package com.util.skinnable.support.compat

import com.airbnb.lottie.model.KeyPath
import com.util.skin.library.SkinManager
import com.util.skin.library.model.SkinConfig
import com.util.skinnable.support.compat.lottie.LottieSkin
import com.util.skinnable.support.compat.lottie.LottieSkinInterceptor
import com.util.skinnable.support.compat.lottie.SkinLottieColorBinding
import com.util.skinnable.support.compat.res.SkinCompatVectorResources
import com.util.skinnable.support.compat.util.SkinCompatDrawableManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LottieAndVectorSkinTest {

    @Before
    fun setUp() {
        val config = SkinConfig.builder().build()
        SkinManager.init(config)
    }

    @Test
    fun testVectorDrawableCacheClearing() {
        // 验证 VectorDrawable 缓存清理逻辑
        SkinCompatVectorResources.clear()
        // 验证 clearCaches() 执行无异常
        SkinCompatDrawableManager.clearCaches()
    }

    @Test
    fun testLottieSkinInterceptorRegistration() {
        val interceptor = LottieSkinInterceptor()
        SkinManager.config.addFactory(interceptor)

        assertTrue(SkinManager.config.factories.contains(interceptor))
    }

    @Test
    fun testLottieColorBindingDataStructure() {
        val keyPath = KeyPath("Layer", "Shape", "Fill")
        val binding = SkinLottieColorBinding(
            keyPath = keyPath,
            colorResId = 0x7f060001,
            colorAttrId = 0x7f040002
        )

        assertEquals(keyPath, binding.keyPath)
        assertEquals(0x7f060001, binding.colorResId)
        assertEquals(0x7f040002, binding.colorAttrId)
    }

    @Test
    fun testLottieSkinAnnotationDefaults() {
        @LottieSkin(
            rawResId = 0x7f110001,
            keyPath = "Layer/Shape",
            colorResId = 0x7f060010,
            autoColor = false
        )
        class DummyAnnotatedClass

        val annotation = DummyAnnotatedClass::class.java.getAnnotation(LottieSkin::class.java)
        assertNotNull(annotation)
        assertEquals(0x7f110001, annotation.rawResId)
        assertEquals("Layer/Shape", annotation.keyPath)
        assertEquals(0x7f060010, annotation.colorResId)
    }
}
