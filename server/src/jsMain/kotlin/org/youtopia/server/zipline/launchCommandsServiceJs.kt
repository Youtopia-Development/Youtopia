package org.youtopia.server.zipline

import app.cash.zipline.Zipline
import org.youtopia.server.api.Commands
import org.youtopia.server.api.Server

@OptIn(ExperimentalJsExport::class)
@JsExport
fun launchCommandsService() {
    val zipline = Zipline.get()
    zipline.bind<Commands>("commands", Server)
}
