plugins {
    alias(libs.plugins.screaming.plugin.run)
}

dependencies {
    /* PROVIDED */
    compileOnly(libs.paper)
    compileOnly(libs.perworldinventory.kt)  {
        exclude(group="*", module="*")
    }
    compileOnly(libs.perworldinventory.old) {
        exclude(group="*", module="*")
    }

    /* SHADED */
    implementation(libs.bstats)
    implementation(libs.commodore) { // brigadier support for Bukkit/Spigot
        exclude(group="com.mojang", module="brigadier")
    }
}

runTestServer {
    pluginJar(tasks.shadowJar.flatMap { it.archiveFile })

    val versionList = listOf(
        "26.2",
        "26.1.2",
        "1.21.11",
        "1.21.10",
        "1.21.8",
        "1.21.4",
        "1.21.3",
        "1.21.1",
        "1.20.6",
        "1.20.4",
        "1.20.2",
        "1.20.1",
        "1.19.4",
        "1.19.3",
        "1.18.2",
        "1.17.1",
        "1.16.5",
        "1.15.2",
        "1.14.4",
        "1.13.2",
        "1.12.2",
        "1.11.2",
        "1.10.2",
        "1.9.4",
        "1.8.8",
    )

    versions(org.screamingsandals.gradle.run.config.Platform.PAPER, versionList) {
        jvmArgs("-Dio.papermc.paper.suppress.sout.nags=true", "-DPaper.IgnoreJavaVersion=true")
        serverProperties {
            onlineMode(false)
            property("level-type", "flat")
        }
    }

    versions(org.screamingsandals.gradle.run.config.Platform.SPIGOT, versionList) {
        jvmArgs("-DIReallyKnowWhatIAmDoingISwear")
        serverProperties {
            onlineMode(false)
            property("level-type", "flat")
        }
    }
}