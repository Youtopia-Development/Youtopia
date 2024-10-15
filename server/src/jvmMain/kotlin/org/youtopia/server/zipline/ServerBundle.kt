package org.youtopia.server.zipline

import app.cash.zipline.EngineApi
import app.cash.zipline.QuickJs
import kotlinx.coroutines.CoroutineDispatcher
import org.youtopia.server.api.Commands

data class ServerBundle @OptIn(EngineApi::class) constructor(
    val commands: Commands,
    val dispatcher: CoroutineDispatcher,
    val quickJs: QuickJs,
)
