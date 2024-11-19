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
    maven {
        name = "Zeenea Connector SDK"
        url = uri("https://maven.pkg.github.com/zeenea/public-connector-sdk")
        credentials {
            username =
                System.getenv("GITHUB_ACTOR") ?: project.findProperty("github.actor") as String?
            password =
                System.getenv("GITHUB_TOKEN") ?: project.findProperty("github.token") as String?
        }
    }
}

dependencies {
    compileOnly(libs.zeenea.public.connector.sdk)
    testImplementation(libs.zeenea.public.connector.sdk)
    annotationProcessor(libs.pf4j)

    compileOnly(libs.jetbrains.annotations)

    /*
     * Logs
     */
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
            "Implementation-Title" to "Example Zeenea Connector Plugin",
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

