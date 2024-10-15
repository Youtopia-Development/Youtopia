package org.youtopia.server.zipline

import app.cash.zipline.EngineApi
import app.cash.zipline.Zipline
import app.cash.zipline.loader.DefaultFreshnessCheckerNotFresh
import app.cash.zipline.loader.LoadResult
import app.cash.zipline.loader.ManifestVerifier.Companion.NO_SIGNATURE_CHECKS
import app.cash.zipline.loader.ZiplineHttpClient
import app.cash.zipline.loader.ZiplineLoader
import com.badlogic.gdx.Gdx
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

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

suspend fun launchZipline(
    dispatcher: CoroutineDispatcher,
    path: String?,
): Zipline {
    val localDirectoryHttpClient = object : ZiplineHttpClient() {
        override suspend fun download(
            url: String,
            requestHeaders: List<Pair<String, String>>,
        ): ByteString {
            val file = url.substringAfterLast("/")
            return (path?.let { Gdx.files.absolute("$it/$file") } ?: Gdx.files.internal("localServer/$file"))
                .readBytes()
                .toByteString()
        }
    }
    val loader = ZiplineLoader(
        dispatcher = dispatcher,
        manifestVerifier = NO_SIGNATURE_CHECKS,
        httpClient = localDirectoryHttpClient,
    )
    return when (val result = loader.loadOnce(
        applicationName = "test",
        freshnessChecker = DefaultFreshnessCheckerNotFresh,
        manifestUrl = "https://localhost/manifest.zipline.json",
    )) {
        is LoadResult.Success -> result.zipline
        is LoadResult.Failure -> throw result.exception
    }
}
