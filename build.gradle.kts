/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

plugins {
    java
    distribution
    alias(libs.plugins.spotless)
}

group = "zeenea.connector.example"
version = System.getenv("VERSION") ?: "dev"
description = "Example Zeenea Connector Plugin"

repositories {
    mavenCentral()
}

dependencies {
    /*
     * Includes the public-connector-sdk as a jar file in the lib folder.
     * Currently, the maven repository containing the jar is private.
     * So the dependency can be added as a file in the project.
     * You will find the jar in the public-connector-sdk-version.jar in the folder lib of the scanner.
     */
    val jarFiles = fileTree("${rootDir}/lib") {
        include("*.jar")
        exclude("*-javadoc.jar")
    }
    compileOnly(jarFiles)
    testImplementation(jarFiles)

    /*
     * Include the PF4J library that manage the plugins.
     * It should not be included in the release by used by the annotation processing.
     */
    compileOnly(libs.pf4j)
    testImplementation(libs.pf4j)
    annotationProcessor(libs.pf4j)

    /*
     * Jackson library for JSON parsing.
     */
    implementation(platform(libs.jackson.bom))
    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jdk8)
    implementation(libs.jackson.datatype.jsr310)

    /*
     * Extra code need for the implementation of the example.
     */
    implementation(project(":log"))
    implementation(project(":filter"))
    implementation(project(":custom-property"))

    /*
     * Tests dependencies.
     */
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.assertj)

    /*
     * Test logs dependencies.
     */
    testRuntimeOnly(libs.jcl.over.slf4j)
    testRuntimeOnly(libs.logback.classic)
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
            "Implementation-Title" to project.description,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Zeenea <support@zeenea.com>"
        )
    }
}

/*
* Define the plugin layout.
*/
distributions {
    main {
        contents {
            from(tasks.compileJava) {
                into("classes")
            }
            from(tasks.processResources) {
                into("classes")
            }
            from(configurations.runtimeClasspath) {
                into("lib")
                // We explicitly exclude these libraries which could have been added by recursive dependencies.
                exclude("slf4j-api*.jar")
                exclude("commons-logging*.jar")
            }
            from("$projectDir/src/main/plugin") {
                expand("project_version" to project.version)
                filteringCharset = "UTF-8"
            }
            into("/")
        }
    }
}

tasks.distTar {
    enabled = false
}

