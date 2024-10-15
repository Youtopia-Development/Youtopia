package org.youtopia

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import org.youtopia.server.zipline.initialiseServerBundles
import org.youtopia.utils.getFileHandle

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        initialiseServerBundles()

        addScreen(GameScreen())
        setScreen<GameScreen>()
    }

    private fun initialiseServerBundles() {
        val modsFolder = getFileHandle("MODS")
        if (!modsFolder.exists() || !modsFolder.isDirectory) {
            modsFolder.delete()
            modsFolder.mkdirs()
        }
        initialiseServerBundles(modsFolder.file().absolutePath)
    }
}
