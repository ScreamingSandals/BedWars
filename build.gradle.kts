import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.freefair.gradle.plugins.lombok.LombokPlugin
import org.screamingsandals.gradle.builder.BuilderPlugin
import org.screamingsandals.gradle.builder.MavenUtilities
import org.screamingsandals.gradle.builder.Utilities
import org.screamingsandals.gradle.builder.JavadocUtilities
import org.screamingsandals.gradle.slib.SLibPlugin
import org.screamingsandals.gradle.slib.SLibExtension

plugins {
    alias(libs.plugins.screaming.plugin.builder) apply false
    alias(libs.plugins.screaming.plugin.slib) apply false
    alias(libs.plugins.lombok) apply false
}

defaultTasks("clean", "build", "shadowJar")

subprojects {
    apply<JavaPlugin>()
    apply<BuilderPlugin>()
    apply<LombokPlugin>()

    var mavenPublication: MavenPublication? = null
    Utilities.configureLicenser(project)
    if (project.name != "BedWars-common") {
        Utilities.configureSourceJarTasks(project)
        mavenPublication = MavenUtilities.setupPublishing(project).publication
        if (!version.toString().endsWith("-SNAPSHOT") && project.name == "BedWars-API") {
            JavadocUtilities.configureJavadocTasks(project)
        }
        MavenUtilities.setupMavenRepositoriesFromProperties(project)
    }

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.screamingsandals.org/public/") }
        maven { url = uri("https://repo.papermc.io/repository/maven-snapshots/") }
        maven { url = uri("https://repo.onarandombox.com/content/groups/public")  }
        maven { url = uri("https://repo.codemc.org/repository/maven-public/")  }
        maven { url = uri("https://repo.alessiodp.com/releases/")  }
    }

    dependencies {
        "compileOnly"(rootProject.libs.jetbrains.annotations)
    }

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:deprecation")
    }

    if (project.name == "BedWars-protocol") {
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

    Utilities.configureShadowPlugin(project, mavenPublication)

    configurations.all {
        // Check for updates every build
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }

    tasks.withType<ShadowJar> {
        relocate("com.zaxxer", "org.screamingsandals.bedwars.lib.HikariCP")
        relocate("org.bstats", "org.screamingsandals.bedwars.lib.ext.bstats")
        relocate("org.spongepowered.configurate", "org.screamingsandals.bedwars.lib.ext.configurate")
        relocate("org.yaml.snakeyaml", "org.screamingsandals.bedwars.lib.ext.snakeyaml")
        relocate("io.leangen.geantyref", "org.screamingsandals.bedwars.lib.ext.geantyref")
        relocate("cloud.commandframework", "org.screamingsandals.bedwars.lib.ext.cloud")
        relocate("me.lucko.commodore", "org.screamingsandals.bedwars.lib.ext.commodore")

        if (System.getenv("BUILD_NUMBER") != null) {
            archiveClassifier.set(System.getenv("BUILD_NUMBER"))
        } else {
            archiveClassifier.set("dev")
        }
    }
}
