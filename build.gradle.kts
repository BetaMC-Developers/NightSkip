import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    kotlin("jvm") version "2.2.10"
    id("com.gradleup.shadow") version "8.3.6"
}

group = "org.betamc.nightskip"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repository.johnymuffin.com/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "2.2.10"))
    compileOnly("com.legacyminecraft.poseidon:poseidon-craftbukkit:1.1.10-250328-1731-f67a8e3")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

tasks.shadowJar {
    isEnableRelocation = true
    relocationPrefix = "org.betamc.nightskip.libs"
    archiveClassifier = ""
}