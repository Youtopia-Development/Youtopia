package org.youtopia.android

import android.os.Bundle
import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import org.youtopia.Main
import org.youtopia.utils.setDefaultFileProperties

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(Main(), AndroidApplicationConfiguration().apply {
            setDefaultFileProperties(path = "", type = Files.FileType.External)
            useImmersiveMode = true
        })
    }
}
