import io.freefair.gradle.plugins.lombok.LombokPlugin
import org.screamingsandals.gradle.builder.*
import org.screamingsandals.gradle.slib.SLibPlugin
import org.screamingsandals.gradle.slib.SLibExtension

plugins {
    alias(libs.plugins.screaming.plugin.builder) apply false
    alias(libs.plugins.screaming.plugin.slib) apply false
    alias(libs.plugins.lombok) apply false
}

defaultTasks("clean", "build")

subprojects {
    apply<JavaPlugin>()
    apply<BuilderPlugin>()
    apply<LombokPlugin>()

    repositories {
        mavenCentral()
        maven("https://repo.screamingsandals.org/public/")
        maven("https://repo.papermc.io/repository/maven-snapshots/")
        maven("https://repo.onarandombox.com/content/groups/public")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://repo.alessiodp.com/releases/")
    }

    dependencies {
        "compileOnly"(rootProject.libs.jetbrains.annotations)
    }

    configureJavac(JavaVersion.VERSION_11)

    val buildNumber = System.getenv("BUILD_NUMBER") ?: "dev"

    tasks.withType<Jar> {
        archiveClassifier.set(buildNumber)
    }

    if (project.name != "BedWars-protocol") {
        configureShadowPlugin {
            relocate("com.zaxxer", "org.screamingsandals.bedwars.lib.HikariCP")
            relocate("org.bstats", "org.screamingsandals.bedwars.lib.ext.bstats")
            relocate("org.spongepowered.configurate", "org.screamingsandals.bedwars.lib.ext.configurate")
            relocate("org.yaml.snakeyaml", "org.screamingsandals.bedwars.lib.ext.snakeyaml")
            relocate("io.leangen.geantyref", "org.screamingsandals.bedwars.lib.ext.geantyref")
            relocate("cloud.commandframework", "org.screamingsandals.bedwars.lib.ext.cloud")
            relocate("me.lucko.commodore", "org.screamingsandals.bedwars.lib.ext.commodore")
        }
    }

    configureLicenser()
    if (project.name != "BedWars-common") { // do not publish the common artifact, only API, protocol, platform artifacts and universal artifact
        val buildSources = project.name == "BedWars-API" || project.name == "BedWars-protocol"
        if (buildSources) {
            configureSourcesJar(configureShadedSourcesInclude=(project.name == "BedWars-API"))
        }
        val buildJavadoc = buildSources && project.name == "BedWars-API"
        if (buildJavadoc) {
            configureJavadocTasks(useSourcesJarAsInput=true)
        }
        setupMavenPublishing(
            addSourceJar=buildSources,
            addJavadocJar=(buildJavadoc && (!version.toString().endsWith("-SNAPSHOT") || System.getenv("FORCE_JAVADOC") == "true")),
        ) {
            pom {
                name.set("BedWars")
                description.set("Flexible BedWars minigame plugin for Minecraft: Java Edition")
                url.set("https://github.com/ScreamingSandals/BedWars")
                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://github.com/ScreamingSandals/BedWars/blob/master/LICENSE")
                    }
                }

                properties.put("build.number", buildNumber)
            }
        }
        setupMavenRepositoriesFromProperties()
    }

    // TODO: check if this is needed (and probably remove it later)
    configurations.all {
        // Check for updates every build
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }

    if (project.name == "BedWars-protocol") {
        // Not an slib project
        return@subprojects
    }

    apply<SLibPlugin>()

    extensions.configure<SLibExtension> {
        version(rootProject.libs.versions.screaming.lib)

        multiModulePlatforms("BedWars-%s", "bukkit")
        multiModuleUniversalSubproject("BedWars") // custom name
        // This also means we cannot create class Wrapper and package types in package org.screamingsandals.bedwars.api
        multiModuleApiSubproject("BedWars-API", "org.screamingsandals.bedwars.api")
        useApiConfigurationInsteadOfImplementation(true)

        additionalContent {
            module("cloud")
            module("hologram")
            module("placeholders")
            module("sidebar")
            module("healthindicator")
            module("npc")
            module("signs")
            module("economy")
            module("ai")
            module("fakedeath")

            lang()
            singleModule("cloud-extras")

            simpleInventories {
                version(rootProject.libs.versions.simple.inventories)
            }
        }
    }
}
