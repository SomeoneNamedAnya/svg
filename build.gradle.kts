import org.gradle.api.tasks.bundling.War

plugins {
    java
    war
    id("org.teavm") version "0.15.0"
}

group = "org.app"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(teavm.libs.jsoApis)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).apply {
        charSet = "UTF-8"
        docEncoding = "UTF-8"
    }
    isFailOnError = true
}

teavm {
    all {
        mainClass = "org.app.web.GraphWebApp"
    }
    js {
        addedToWebApp = true
        targetFileName = "app.js"
        sourceMap = true
        obfuscated = false
    }
}

tasks.war {
    archiveFileName.set("function-plotter.war")
}

val standaloneOutput = layout.buildDirectory.file("dist/function-plotter.html")
val warTask = tasks.named<War>("war")

tasks.register("standaloneHtml") {
    group = "build"
    description = "Создаёт автономный function-plotter.html"

    dependsOn(warTask)

    inputs.file(warTask.flatMap { it.archiveFile })
    outputs.file(standaloneOutput)

    doLast {
        val warArchive = warTask.get().archiveFile.get().asFile
        val warContents = zipTree(warArchive)

        val indexFile = warContents.matching {
            include("index.html")
        }.singleFile

        val javaScriptFile = warContents.matching {
            include("js/app.js")
        }.singleFile

        val html = indexFile.readText(Charsets.UTF_8)
        val javaScript = javaScriptFile.readText(Charsets.UTF_8)
        val externalScriptTag = "<script src=\"js/app.js\"></script>"

        check(html.contains(externalScriptTag)) {
            "В index.html не найден тег подключения js/app.js"
        }

        val safeJavaScript = javaScript
            .replace("</script>", "<\\/script>")
            .replace(Regex("(?m)^//# sourceMappingURL=.*$"), "")

        val embeddedScript = "<script>\n$safeJavaScript\n</script>"
        val standaloneHtml = html.replace(externalScriptTag, embeddedScript)
        val outputFile = standaloneOutput.get().asFile

        outputFile.parentFile.mkdirs()
        outputFile.writeText(standaloneHtml, Charsets.UTF_8)

        logger.lifecycle("Готовый файл: ${outputFile.absolutePath}")
    }
}
