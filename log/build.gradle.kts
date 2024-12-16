/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

plugins {
    `java-library`
    alias(libs.plugins.spotless)
}

group = "zeenea.connector.example"
version = System.getenv("VERSION") ?: "dev"
description = "Zeenea Example Log Library"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.jetbrains.annotations)

    /*
     * Logs
     */
    compileOnly(libs.slf4j.api)
    testImplementation(libs.slf4j.api)
    testRuntimeOnly(libs.jcl.over.slf4j)
    testRuntimeOnly(libs.logback.classic)

    /*
     * Tests
     */
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

spotless {
    java {
        googleJavaFormat()
        targetExclude("build/generated/**")
    }
}

tasks.withType<JavaCompile> {
    with(options) {
        encoding = "UTF-8"
        compilerArgs.add("-Xlint:unchecked")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Zeenea Example Log Library",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Zeenea <support@zeenea.com>"
        )
    }
}

