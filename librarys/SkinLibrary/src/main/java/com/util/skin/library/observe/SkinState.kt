package com.util.skin.library.observe

import com.util.skin.library.loader.SkinStrategy

data class SkinState(
    val currentStrategy: SkinStrategy? = null,
    val timestamp: Long = System.currentTimeMillis()
)