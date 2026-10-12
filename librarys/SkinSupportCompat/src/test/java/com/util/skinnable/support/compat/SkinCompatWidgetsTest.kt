package com.util.skinnable.support.compat

import com.util.skin.library.SkinManager
import com.util.skin.library.model.SkinConfig
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class SkinCompatWidgetsTest {

    @Before
    fun setUp() {
        val config = SkinConfig.builder().build()
        SkinManager.init(config)
    }

    @Test
    fun testViewFactoryCreationNames() {
        val factory = SkinAppCompatViewFactory()
        assertNotNull(factory)
    }
}
