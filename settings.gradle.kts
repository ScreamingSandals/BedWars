pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.screamingsandals.org/public/")
        }
        maven {
            // TODO: remove repository when (if) uploaded to gradle plugin portal
            url = uri("https://maven.neoforged.net/releases")
            content {
                includeGroup("net.neoforged.licenser")
            }
        }
    }
}

rootProject.name = "BedWars-parent"
include(":BedWars-API")
project(":BedWars-API").projectDir = file("api")
include(":BedWars")
project(":BedWars").let {
    it.projectDir = file("plugin/universal")
    it.projectDir.mkdirs()
}
include(":BedWars-protocol")
project(":BedWars-protocol").projectDir = file("protocol")
include(":BedWars-common")
project(":BedWars-common").projectDir = file("plugin/common")
include(":BedWars-bukkit")
project(":BedWars-bukkit").projectDir = file("plugin/bukkit")
