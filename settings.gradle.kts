pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.screamingsandals.org/public/")
    }
}

rootProject.name = "BedWars-parent"
setupProject("BedWars-API", "api")
setupProject("BedWars-NMS", "nms")
setupProject("BedWars", "plugin")

fun setupProject(name: String, folder: String, mkdir: Boolean = false) {
    include(name)
    project(":$name").let {
        it.projectDir = file(folder)
        if (mkdir) {
            it.projectDir.mkdirs()
        }
    }
}