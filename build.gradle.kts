plugins {
    id("java")
    kotlin("jvm") version "2.3.10"
    id("com.gradleup.shadow") version "9.3.1"
}

group = "org.betamc"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation(kotlin("stdlib", "2.3.10"))
    compileOnly("com.github.BetaMC-Developers:Tsunami:1.0.7")
}

kotlin {
    jvmToolchain(8)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    expand(mapOf(
        "version" to project.version
    ))
}

tasks.shadowJar {
    enableAutoRelocation = true
    relocationPrefix = "org.betamc.nightskip.libs"
    archiveClassifier = ""
}