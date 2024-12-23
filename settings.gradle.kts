pluginManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
//        maven("https://repo.screamingsandals.org/public/")
        // TODO: remove repository when (if) uploaded to gradle plugin portal
        maven("https://maven.neoforged.net/releases") {
            content {
                includeGroup("net.neoforged.licenser")
            }
        }
    }
}

rootProject.name = "BedWars-parent"
setupProject("BedWars-API", "api")
setupProject("BedWars-protocol", "protocol")
setupProject("BedWars-common", "plugin/common")
setupProject("BedWars-bukkit", "plugin/bukkit")
setupProject("BedWars", "plugin/universal", mkdir=true)

fun setupProject(name: String, folder: String, mkdir: Boolean = false) {
    include(name)
    project(":$name").let {
        it.projectDir = file(folder)
        if (mkdir) {
            it.projectDir.mkdirs()
        }
    }
}