package org.youtopia.server.zipline

import app.cash.zipline.EngineApi
import app.cash.zipline.Zipline
import app.cash.zipline.loader.DefaultFreshnessCheckerNotFresh
import app.cash.zipline.loader.LoadResult
import app.cash.zipline.loader.ManifestVerifier.Companion.NO_SIGNATURE_CHECKS
import app.cash.zipline.loader.ZiplineHttpClient
import app.cash.zipline.loader.ZiplineLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import okio.BufferedSource
import okio.ByteString
import okio.FileSystem
import okio.Path.Companion.toPath
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

/**
 * Whether we want to use the default server build location (`server/build/zipline/Development`).
 * When turned on, you won't need to copy the server bytecode into `assets/localServer` after every build.
 * Only works on desktop as /build directory is not available on mobile.
 */
const val DEV = false

@OptIn(EngineApi::class)
fun getServerBundle(modDirectory: String? = null): ServerBundle {
    val executorService: ExecutorService = Executors.newFixedThreadPool(1) { Thread(it, "Zipline-${Random.nextInt()}") }
    val dispatcher = executorService.asCoroutineDispatcher()
    return runBlocking(dispatcher) {
        val zipline = launchZipline(dispatcher, modDirectory)
        ServerBundle(
            zipline.take("commands"),
            dispatcher,
            zipline.quickJs,
        )
    }
}

private suspend fun launchZipline(
    dispatcher: CoroutineDispatcher,
    path: String?,
): Zipline {
    val localDirectoryHttpClient = object : ZiplineHttpClient() {
        override suspend fun download(
            url: String,
            requestHeaders: List<Pair<String, String>>,
        ): ByteString {
            val file = url.substringAfterLast("/")
            return FileSystem.SYSTEM.read(
                (path ?: if (DEV) "../server/build/zipline/Development" else "../assets/localServer").toPath() / file,
                BufferedSource::readByteString,
            )
        }
    }
    val loader = ZiplineLoader(
        dispatcher = dispatcher,
        manifestVerifier = NO_SIGNATURE_CHECKS,
        httpClient = localDirectoryHttpClient,
    )
    return when (val result = loader.loadOnce(
        path?.substringAfterLast('/') ?: "Main",
        DefaultFreshnessCheckerNotFresh,
        "https://localhost/manifest.zipline.json",
    )) {
        is LoadResult.Success -> result.zipline
        is LoadResult.Failure -> throw result.exception
    }
}
