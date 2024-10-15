package org.youtopia.utils

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

private var defaultPath: String = ""
private var defaultFileType: Files.FileType = Files.FileType.Absolute

fun setDefaultFileProperties(path: String, type: Files.FileType) {
    defaultFileType = type
    defaultPath = path
}

fun getFileHandle(name: String): FileHandle = getFileHandle(defaultFileType, defaultPath, name)

private fun getFileHandle(type: Files.FileType, basePath: String, name: String): FileHandle = when (type) {
    Files.FileType.Classpath -> Gdx.files.classpath(basePath + name)
    Files.FileType.Internal -> Gdx.files.internal(basePath + name)
    Files.FileType.External -> Gdx.files.external(basePath + name)
    Files.FileType.Absolute -> Gdx.files.absolute(basePath + name)
    Files.FileType.Local -> Gdx.files.local(basePath + name)
    else -> error("Unsupported file type $type")
}
