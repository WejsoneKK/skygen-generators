import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    alias(libs.plugins.eldoria.paper)
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.okaeri.cloud/releases")
    maven("https://repo.eternalcode.pl/releases")
    maven("https://repo.fancyinnovations.com/releases")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.api.get())
    implementation(libs.kotlin.stdlib)

    paperLibrary(libs.litecommands)
    paperLibrary(libs.eternalcommons.loom)
    paperLibrary(libs.eternalcommons.adventure)
    paperLibrary(libs.multification.okaeri)
    paperLibrary(libs.multification.paper)
    paperLibrary(libs.okaeri.placeholders.bukkit)
    paperLibrary(libs.okaeri.placeholders.adventure)
    paperLibrary(libs.okaeri.configs.serdes.adventure)
    paperLibrary(libs.okaeri.configs.serdes.bukkit)
    paperLibrary(libs.okaeri.configs.yam.bukkit)
    paperLibrary(libs.okaeri.configs.yaml)
    paperLibrary(libs.okaeri.configs.json.simple)
    paperLibrary(libs.triumph.gui)
    paperLibrary(libs.ormlite.jdbc)
    paperLibrary(libs.ormlite.core)
    paperLibrary(libs.h2database)
    paperLibrary(libs.hikaricp)
    paperLibrary(libs.postgresql)
    paperLibrary(libs.mariadb)
    paperLibrary(libs.mongodb.driver.sync)
    paperLibrary(libs.xseries)

    compileOnly(libs.fancyholograms)
}
paper {
    name = "wSkyGenGenerators"
    version = project.version.toString()
    main = "eu.wejsonekk.skygen.skygengenerator.SkyGenGeneratorPlugin"
    loader = "eu.wejsonekk.skygen.skygengenerator.SkyGenGeneratorPluginLoader"
    bootstrapper = "eu.wejsonekk.skygen.skygengenerator.SkyGenGeneratorPluginBootstrap"
    apiVersion = "1.21"
    author = "WejsoneKKEU"
    contributors = listOf("ClaudeCode")
    description = "Generators for SkyGen modes"
    hasOpenClassloader = true
    generateLibrariesJson = true
    serverDependencies {
        register("FancyHolograms") {
            required = false
            load = net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder.AFTER
        }
    }
}
kotlin {
    jvmToolchain(23)
}
tasks {
    build {
        dependsOn(shadowJar)
    }
}
