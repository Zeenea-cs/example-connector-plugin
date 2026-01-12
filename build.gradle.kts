plugins {
    java
    distribution
    id("com.diffplug.spotless") version "6.25.0"
    id("com.github.spotbugs") version "6.0.14"
}

group = "zeenea.connector.example"
version = System.getenv("VERSION") ?: "dev"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

spotless {
    java {
        targetExclude("build\\generated\\**\\*.java")
        googleJavaFormat()
    }
}

spotbugs {
    val excludeFile = file("${rootDir}/spotbug-exclude.xml")
    if (excludeFile.exists()) {
        excludeFilter.set(excludeFile)
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

tasks.distTar {
    enabled = false
}

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

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "Zeenea Connector SDK"
        url = uri("https://maven.pkg.github.com/zeenea/*")
        credentials {
            username =
                System.getenv("GITHUB_ACTOR") ?: project.findProperty("github.actor") as String?
            password =
                System.getenv("GITHUB_TOKEN") ?: project.findProperty("github.token") as String?
        }
    }
}

tasks.test {
    systemProperty("approvaltests.approvals.baseDirectory", "src/test/resources")
}

dependencies {
    // Zeenea public SDK
    val publicConnectorSdkVersion: String by project
    compileOnly(group = "zeenea", name = "public-connector-sdk", version = publicConnectorSdkVersion)
    testImplementation(group = "zeenea", name = "public-connector-sdk", version = publicConnectorSdkVersion)

    val pf4jVersion: String by project
    annotationProcessor(group = "org.pf4j", name = "pf4j", version = pf4jVersion)

    val jetbrainsAnnotationsVersion: String by project
    compileOnly(
        group = "org.jetbrains",
        name = "annotations",
        version = jetbrainsAnnotationsVersion
    )

    val javaTuples: String by project
    implementation(
        group = "org.javatuples",
        name = "javatuples",
        version = javaTuples
    )

    val spotbugsAnnotations = "com.github.spotbugs:spotbugs-annotations:${spotbugs.toolVersion.get()}"
    compileOnly(spotbugsAnnotations)
    testImplementation(spotbugsAnnotations)

    val lombokVersion: String by project
    compileOnly(group = "org.projectlombok", name = "lombok", version = lombokVersion)
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = lombokVersion)

    // Logs
    val slf4jVersion: String by project
    testRuntimeOnly(group = "org.slf4j", name = "jcl-over-slf4j", version = slf4jVersion)

    val logbackVersion: String by project
    testRuntimeOnly(group = "ch.qos.logback", name = "logback-classic", version = logbackVersion)

    /*
     * Tests
     */
    val junitVersion: String by project
    testImplementation(platform("org.junit:junit-bom:${junitVersion}"))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params")
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine")

    val assertjVersion: String by project
    testImplementation(group = "org.assertj", name = "assertj-core", version = assertjVersion)

    val mockitoVersion: String by project
    testImplementation(group = "org.mockito", name = "mockito-core", version = mockitoVersion)
    testImplementation(group = "org.mockito", name = "mockito-junit-jupiter", version = mockitoVersion)

    val jacksonVersion: String by project
    testImplementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    testImplementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = jacksonVersion)
    testImplementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jdk8", version = jacksonVersion)

    val approvaltestsVersion: String by project
    testImplementation(group = "com.approvaltests", name = "approvaltests", version = approvaltestsVersion)
}
