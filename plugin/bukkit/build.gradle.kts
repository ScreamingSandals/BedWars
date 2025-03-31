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
    pluginJar(tasks.shadowJar.get().archiveFile)

    versions(
        org.screamingsandals.gradle.run.config.Platform.PAPER,
        listOf(
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
    ) {
        jvmArgs("-Dio.papermc.paper.suppress.sout.nags=true", "-DPaper.IgnoreJavaVersion=true")
        serverProperties {
            onlineMode(false)
        }
    }
}

tasks.register("rewriteGeneratedFile") {
    doLast {
        val generatedFile = file("build/classes/java/main/plugin.yml")
        if (generatedFile.exists()) {
            val content = generatedFile.readText()
            val updatedContent = content.replace("org.screamingsandals.bedwars.BedWarsPlugin_BukkitImpl", "nisan11.doorwars.BedWarsPlugin_BukkitImpl")
            generatedFile.writeText(updatedContent)
        }
    }
}

tasks.named("compileJava") {
    finalizedBy("rewriteGeneratedFile")
}