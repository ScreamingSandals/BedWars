import org.apache.tools.ant.filters.*

plugins {
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.screaming.plugin.run)
}

dependencies {
    /* PROVIDED */
    compileOnly(libs.vault)
    compileOnly(libs.multiverse.core)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.perworldinventory.kt) {
        exclude(group="*", module="*")
    }
    compileOnly(libs.perworldinventory.old) {
        exclude(group="*", module="*")
    }
    compileOnly(libs.netty)
    compileOnly(libs.citizens)
    compileOnly(libs.parties.api)
    compileOnly(libs.viaversion)

    /* SHADED */
    implementation(project(":BedWars-API"))
    implementation(project(":BedWars-NMS"))
    implementation(libs.hikari)
    implementation(libs.simpleinventories)
    implementation(libs.bstats)
    implementation(libs.mclogs.api) {
        exclude(group="*", module="*")
    }
}

tasks.processResources {
    val pluginVersion = project.version.toString()

    filesMatching("plugin.yml") {
        filter(mapOf("tokens" to mapOf(
            "version" to pluginVersion
        )), ReplaceTokens::class.java)
    }

}

tasks.jar {
    manifest {
        attributes(mapOf(
                "paperweight-mappings-namespace" to "mojang"
        ))
    }
}

buildConfig {
    className("VersionInfo")
    packageName("org.screamingsandals.bedwars")

    buildConfigField("String", "NAME", "\"${project.name}\"")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
    buildConfigField("String", "BUILD_NUMBER", "\"branch-0.2.x/${providers.environmentVariable("BUILD_NUMBER").orElse("custom").get()}\"")
}

runTestServer {
    pluginJar(tasks.shadowJar.flatMap { it.archiveFile })

    versions(
        org.screamingsandals.gradle.run.config.Platform.PAPER,
        listOf(
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
    ) {
        jvmArgs("-Dio.papermc.paper.suppress.sout.nags=true", "-DPaper.IgnoreJavaVersion=true")
        serverProperties {
            onlineMode(false)
            property("level-type", "flat")
        }
    }
}