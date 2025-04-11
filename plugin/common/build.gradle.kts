plugins {
    alias(libs.plugins.buildconfig)
}

dependencies {
    /* PROVIDED */
    compileOnly(libs.multiverse)
    compileOnly(libs.parties.api)

    /* SHADED */
    implementation("org.screamingsandals.language.bedwars:BedWarsLanguage:${Regex("^\\d+\\.\\d+").find(project.version.toString())?.value}-SNAPSHOT")
    implementation(libs.hikari)

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