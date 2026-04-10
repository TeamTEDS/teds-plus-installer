package io.github.teamteds.tedsmodpacksinstaller

import com.google.gson.JsonObject
import java.nio.file.Path
import java.time.Instant

class PackVersion(val modpack: Modpack, val data: JsonObject) {
    val packVersion: String
    val gameVersion: String
    val loader: Loader
    val datePublished: Instant

    init {
<<<<<<< HEAD
        val versionNumber = data["version_number"].asString
        loader = if ('-' in versionNumber && '+' !in versionNumber) {
            packVersion = versionNumber.substringBefore('-')
            gameVersion = versionNumber.substringAfterLast('-')
            if (versionNumber.count { it == '-' } > 1) {
                versionNumber.substringAfter('-').substringBeforeLast('-')
            } else {
                "FABRIC"
            }
        } else {
            packVersion = versionNumber.substringBefore('+')
            gameVersion = versionNumber.substringAfter('+').substringBeforeLast('.')
            if (versionNumber.substringAfterLast('.') == "fabric") {
                "fabric"
            } else {
                "quilt"
            }
        }.uppercase().let(Loader::valueOf)
=======
        val (packVer, gameVer, ldr) = parseVersion(data["version_number"].asString)
        packVersion = packVer
        gameVersion = gameVer
        loader = ldr
>>>>>>> upstream/main
        datePublished = Instant.parse(data["date_published"].asString)
    }

    private fun parseVersion(versionNumber: String): Triple<String, String, Loader> = when {
        '+' in versionNumber -> versionNumber.substringAfter('+').removePrefix("mc").let {
            Triple(
                versionNumber.substringBefore('+'),
                it.substringBeforeLast('.'),
                Loader.valueOf(it.substringAfterLast('.').uppercase())
            )
        }
        '-' in versionNumber -> {
            val parts = versionNumber.split('-')
            when (parts.size) {
                2 -> Triple(parts[0], parts[1], Loader.FABRIC)
                3 -> Triple(parts[0], parts[2], Loader.valueOf(parts[1].uppercase()))
                else -> Triple(versionNumber, versionNumber, Loader.FABRIC)
            }
        }
        else -> Triple(versionNumber, versionNumber, Loader.FABRIC)
    }

    val launcherFolderPath = "${modpack.id}/$packVersion-$gameVersion-$loader"
    val launcherVersionId = "${modpack.id}-$packVersion-$gameVersion-$loader"
    val launcherProfileId = "${modpack.id}-$gameVersion-$loader"

    fun install(destination: Path, progressHandler: ProgressHandler) =
        PackInstaller(this, destination, progressHandler)
            .use(PackInstaller::install)
}
