val moduleName by extra("dev.faststats.hytale")

val libsDir: Directory = layout.projectDirectory.dir("libs")
val hytaleServerJar: RegularFile = libsDir.file("HytaleServer.jar")
val credentialsFile: RegularFile = layout.projectDirectory.file(".hytale-downloader-credentials.json")
val downloadDir: Provider<Directory> = layout.buildDirectory.dir("download")
val hytaleZip: Provider<RegularFile> = downloadDir.map { it.file("hytale.zip") }

dependencies {
    api(project(":core"))
    compileOnly(files(hytaleServerJar))
}

tasks.register("download-server") {
    group = "hytale"

    doLast {
        if (hytaleServerJar.asFile.exists()) {
            println("HytaleServer.jar already exists, skipping download")
            return@doLast
        }

        val downloaderZip: Provider<RegularFile> = downloadDir.map { it.file("hytale-downloader.zip") }

        libsDir.asFile.mkdirs()
        downloadDir.get().asFile.mkdirs()

        val os = org.gradle.internal.os.OperatingSystem.current()
        val downloaderExecutable = when {
            os.isLinux -> downloadDir.map { it.file("hytale-downloader-linux-amd64") }
            os.isWindows -> downloadDir.map { it.file("hytale-downloader-windows-amd64.exe") }
            else -> throw GradleException("Unsupported operating system: ${os.name}")
        }

        if (!downloaderExecutable.get().asFile.exists()) {
            if (!downloaderZip.get().asFile.exists()) ant.invokeMethod(
                "get", mapOf(
                    "src" to "https://downloader.hytale.com/hytale-downloader.zip",
                    "dest" to downloaderZip.get().asFile.absolutePath
                )
            ) else {
                println("hytale-downloader.zip already exists, skipping download")
            }

            copy {
                from(zipTree(downloaderZip))
                include(downloaderExecutable.get().asFile.name)
                into(downloadDir)
            }
        } else {
            println("Hytale downloader binary already exists, skipping download and extraction")
        }

        if (downloaderZip.get().asFile.delete()) {
            println("Deleted hytale-downloader.zip after extracting binaries")
        }

        downloaderExecutable.get().asFile.setExecutable(true)

        if (!hytaleZip.get().asFile.exists()) {
            val credentials = System.getenv("HYTALE_DOWNLOADER_CREDENTIALS")
            if (!credentials.isNullOrBlank()) {
                if (!credentialsFile.asFile.exists()) {
                    credentialsFile.asFile.writeText(credentials)
                    println("Hytale downloader credentials written from environment variable to ${credentialsFile.asFile.absolutePath}")
                } else {
                    println("Using existing credentials file at ${credentialsFile.asFile.absolutePath}")
                }
            }

            val processBuilder = ProcessBuilder(
                downloaderExecutable.get().asFile.absolutePath,
                "-download-path",
                "hytale",
                "-credentials-path",
                credentialsFile.asFile.absolutePath
            )
            processBuilder.directory(downloadDir.get().asFile)
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()

            process.inputStream.bufferedReader().use { reader ->
                reader.lines().forEach { line ->
                    println(line)
                }
            }

            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw GradleException("Hytale downloader failed with exit code: $exitCode")
            }
        } else {
            println("hytale.zip already exists, skipping download")
        }

        if (hytaleZip.get().asFile.exists()) {
            val serverDir = downloadDir.map { it.dir("Server") }
            copy {
                from(zipTree(hytaleZip))
                include("Server/HytaleServer.jar")
                into(downloadDir)
            }

            val extractedJar = serverDir.map { it.file("HytaleServer.jar") }
            if (extractedJar.get().asFile.exists()) {
                extractedJar.get().asFile.copyTo(hytaleServerJar.asFile, overwrite = true)
                serverDir.get().asFile.deleteRecursively()
            } else {
                throw GradleException("HytaleServer.jar was not found in Server/ subdirectory")
            }

            if (!hytaleServerJar.asFile.exists()) {
                throw GradleException("HytaleServer.jar was not found in hytale.zip")
            }

            hytaleZip.get().asFile.delete()
            println("Deleted hytale.zip after extracting HytaleServer.jar")
        } else {
            throw GradleException(
                "hytale.zip not found at ${hytaleZip.get().asFile.absolutePath}. " +
                        "The downloader may not have completed successfully."
            )
        }
    }
}

tasks.register("update-server") {
    group = "hytale"
    hytaleServerJar.asFile.delete()
    hytaleZip.get().asFile.delete()
    dependsOn(tasks.named("download-server"))
}

tasks.compileJava {
    dependsOn(tasks.named("download-server"))
}