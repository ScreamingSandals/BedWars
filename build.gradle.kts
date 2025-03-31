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

    tasks.withType<Jar> {
        archiveClassifier.set(System.getenv("BUILD_NUMBER") ?: "dev")
    }

    if (project.name != "BedWars-protocol") {
        configureShadowPlugin {
            relocate("com.zaxxer", "nisan11.doorwars.lib.HikariCP")
            relocate("org.bstats", "nisan11.doorwars.lib.ext.bstats")
            relocate("org.spongepowered.configurate", "nisan11.doorwars.lib.ext.configurate")
            relocate("org.yaml.snakeyaml", "nisan11.doorwars.lib.ext.snakeyaml")
            relocate("io.leangen.geantyref", "nisan11.doorwars.lib.ext.geantyref")
            relocate("cloud.commandframework", "nisan11.doorwars.lib.ext.cloud")
            relocate("me.lucko.commodore", "nisan11.doorwars.lib.ext.commodore")
            relocate("org.screamingsandals.bedwars", "nisan11.doorwars")
        }
    }

    configureLicenser()
    if (project.name != "BedWars-common") { // do not publish the common artifact, only API, protocol, platform artifacts and universal artifact
        // TODO: figure out how to relocate api-utils in Javadoc and sourceJar of BedWars-API (to the package defined in SLibExtension)
        val buildSources = project.name == "BedWars-API" || project.name == "BedWars-protocol"
        if (buildSources) {
            configureSourcesJar()
        }
        val buildJavadoc = !version.toString().endsWith("-SNAPSHOT") && project.name == "BedWars-API"
        if (buildJavadoc) {
            configureJavadocTasks()
        }
        setupMavenPublishing(addSourceJar=buildSources, addJavadocJar=buildJavadoc)
        setupMavenRepositoriesFromProperties()
    }

    configureJavac(JavaVersion.VERSION_11)

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
        multiModuleApiSubproject("BedWars-API", "nisan11.doorwars.api")
        useApiConfigurationInsteadOfImplementation(true)
        customRelocatePath("nisan11.doorwars.lib")

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
