plugins {
    alias(libs.plugins.buildconfig)
}

dependencies {
    /* PROVIDED */
    compileOnly(libs.multiverse)
    compileOnly(libs.multiverse5.core)
    compileOnly(libs.parties.api)

    /* SHADED */
    api("org.screamingsandals.language.bedwars:BedWarsLanguage:${Regex("^\\d+\\.\\d+").find(project.version.toString())?.value}-SNAPSHOT")
    implementation(libs.hikari)
    implementation(libs.mclogs.api) {
        exclude(group="*", module="*")
    }

    api(libs.configurate.gson) {
        exclude(group="*", module="*")
    }
    api(libs.configurate.yaml)
    api(project(":BedWars-protocol"))
}

buildConfig {
    className("VersionInfo")
    packageName("org.screamingsandals.bedwars")

    buildConfigField("String", "NAME", "\"${project.name}\"")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
    buildConfigField("String", "BUILD_NUMBER", "\"${System.getenv("BUILD_NUMBER") ?: "custom"}\"")
}