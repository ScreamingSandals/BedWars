import io.freefair.gradle.plugins.lombok.LombokPlugin
import org.screamingsandals.gradle.builder.*

plugins {
    alias(libs.plugins.screaming.plugin.builder) apply false
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
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.onarandombox.com/content/groups/public")
        maven("https://repo.citizensnpcs.co")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://repo.alessiodp.com/releases/")
        maven("https://repo.viaversion.com")
        maven("https://jitpack.io")
    }

    dependencies {
        "compileOnly"(rootProject.libs.jetbrains.annotations)
        "compileOnly"(rootProject.libs.paper)
    }

    configureJavac(JavaVersion.VERSION_1_8)

    val buildNumber = providers.environmentVariable("BUILD_NUMBER").orElse("dev")

    tasks.withType<Jar> {
        archiveClassifier.set(buildNumber)
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:-options")
    }

    configureShadowPlugin {
        relocate("com.zaxxer", "org.screamingsandals.bedwars.lib.HikariCP")
        relocate("org.screamingsandals.simpleinventories", "org.screamingsandals.bedwars.lib.sgui")
        relocate("org.bstats", "org.screamingsandals.bedwars.lib.bstats")
        relocate("me.kcra.takenaka", "org.screamingsandals.bedwars.lib.takenaka")
        relocate("gs.mclo", "org.screamingsandals.bedwars.lib.mclogs")
    }

    configureLicenser()

    if (project.name == "BedWars-API" || project.name == "BedWars") {
        configureSourcesJar()
        val buildJavadoc = project.name == "BedWars-API"
        if (buildJavadoc) {
            configureJavadocTasks()
        }
        setupMavenPublishing(
            addSourceJar=true,
            addJavadocJar=(buildJavadoc && (!version.toString().endsWith("-SNAPSHOT") || System.getenv("FORCE_JAVADOC") == "true")),
        ) {
            pom {
                name.set("BedWars")
                description.set("Flexible BedWars minigame plugin for Minecraft: Java Edition")
                url.set("https://github.com/ScreamingSandals/BedWars")
                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://github.com/ScreamingSandals/BedWars/blob/ver/0.2.x/LICENSE")
                    }
                }

                properties.put("build.number", buildNumber)
            }
        }
        setupMavenRepositoriesFromProperties()
    }
}
