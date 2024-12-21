pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.screamingsandals.org/public/")
        }

    }
}

rootProject.name = "BedWars-parent"
include(":BedWars-API")
project(":BedWars-API").projectDir = file("api")
include(":BedWars")
project(":BedWars").projectDir = file("plugin/universal")
include(":BedWars-protocol")
project(":BedWars-protocol").projectDir = file("protocol")
include(":BedWars-common")
project(":BedWars-common").projectDir = file("plugin/common")
include(":BedWars-bukkit")
project(":BedWars-bukkit").projectDir = file("plugin/bukkit")
