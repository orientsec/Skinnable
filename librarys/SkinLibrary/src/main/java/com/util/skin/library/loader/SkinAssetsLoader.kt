package com.util.skin.library.loader

import android.content.Context
import com.util.skin.library.utils.SKIN_DEPLOY_PATH
import com.util.skin.library.utils.getSkinDir
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SkinAssetsLoader : SkinSDCardLoader() {

    override val type: SkinLoaderStrategyType
        get() = SkinLoaderStrategyType.Assets

    override fun getSkinPath(context: Context, skinName: String): String {
        return copySkinFromAssets(context, skinName)
    }

    override fun getTargetResourceEntryName(context: Context, skinName: String, resId: Int): String? {
        return null
    }

    private fun copySkinFromAssets(context: Context, name: String): String {
        val skinPath = File(getSkinDir(context), name).absolutePath
        try {
            val inputStream = context.assets.open(SKIN_DEPLOY_PATH + File.separator + name)
            val os = FileOutputStream(skinPath)
            var byteCount: Int
            val bytes = ByteArray(1024)
            byteCount = inputStream.read(bytes)
            while (byteCount != -1) {
                os.write(bytes, 0, byteCount)
                byteCount = inputStream.read(bytes)
            }
            os.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return skinPath
    }
}
