@file:JvmName("Lwjgl3Launcher")

package org.youtopia.desktop

import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.utils.SharedLibraryLoader
import org.youtopia.Main
import org.youtopia.utils.setDefaultFileProperties

/** Launches the desktop (LWJGL3) application. */
@Suppress("SpreadOperator", "ArrayPrimitive")
fun main() {
    if (StartupHelper.startNewJvmIfRequired()) return // This handles macOS support and helps on Windows

    val vendor = "qsr"
    val title = "Youtopia"

    val config = Lwjgl3ApplicationConfiguration()

    config.setTitle(title)

    val (basePath, baseType) = if (SharedLibraryLoader.isWindows) {
        (if (System.getProperties().getProperty("os.name") == "Windows XP") {
            "Application Data/.$vendor/$title/"
        } else {
            "AppData/Roaming/.$vendor/$title/"
        }) to FileType.External
    } else if (SharedLibraryLoader.isMac) {
        "Library/Application Support/$title/" to FileType.External
    } else if (SharedLibraryLoader.isLinux) {
        var xdgHome = System.getenv("XDG_DATA_HOME")
        if (xdgHome == null) xdgHome = System.getProperty("user.home") + "/.local/share"
        val titleLinux: String = title.lowercase().replace(' ', '-')
        "$xdgHome/.$vendor/$titleLinux/" to FileType.Absolute
    } else error("TempleOS is not currently supported.")

    setDefaultFileProperties(basePath, baseType)
    config.setPreferencesConfig(basePath, baseType)
    config.setMaximized(true)

    Lwjgl3Application(Main(), Lwjgl3ApplicationConfiguration().apply {
        setTitle(title)
        setWindowedMode(1920, 1080)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
