import java.util.*

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("app.cash.licensee:licensee-gradle-plugin:1.5.0")
    }
}

apply(plugin = "app.cash.licensee")

plugins {
    id("com.diffplug.spotless")
    id("org.mikeneck.graalvm-native-image")
    id("com.github.johnrengelman.shadow")
    id("com.apollographql.apollo3")
    id("com.avast.gradle.docker-compose")
    kotlin("jvm")
}

val cliGroup: String by project
val projectVersion: String by project
val apolloVersion: String by project
val commonsLang3Version: String by project
val cliktVersion: String by project
val bouncycastleVersion: String by project
val jupiterVersion: String by project
val commonsIoVersion: String by project
val auth0JwtVersion: String by project
val vertxVersion: String by project
val slf4jVersion: String by project

group = cliGroup
version = project.properties["cliVersion"] as String? ?: projectVersion

repositories {
    mavenCentral()
    maven(url = "https://pkg.sourceplus.plus/sourceplusplus/protocol")
}

dependencies {
    implementation("com.apollographql.apollo3:apollo-runtime:$apolloVersion")
    api("com.apollographql.apollo3:apollo-api:$apolloVersion")

    implementation("plus.sourceplus:protocol:$projectVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-nop:$slf4jVersion")
    implementation("io.vertx:vertx-core:$vertxVersion")
    implementation("io.vertx:vertx-tcp-eventbus-bridge:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")
    implementation("org.apache.commons:commons-lang3:$commonsLang3Version")
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncycastleVersion")
    implementation("org.bouncycastle:bcpkix-jdk15on:$bouncycastleVersion")
    implementation("commons-io:commons-io:$commonsIoVersion")
    implementation("com.auth0:java-jwt:$auth0JwtVersion")
    implementation("eu.geekplace.javapinning:java-pinning-core:1.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
}

configure<app.cash.licensee.LicenseeExtension> {
    ignoreDependencies("plus.sourceplus", "protocol")
    ignoreDependencies("plus.sourceplus", "protocol-jvm")
    allow("Apache-2.0")
    allow("MIT")
    allowUrl("https://raw.githubusercontent.com/apollographql/apollo-kotlin/main/LICENSE") //MIT
    allowUrl("https://raw.githubusercontent.com/auth0/java-jwt/master/LICENSE") //MIT
    allowUrl("https://www.bouncycastle.org/licence.html") //MIT
}

//todo: shouldn't need to put in src (github actions needs for some reason)
tasks.create("createProperties") {
    if (System.getProperty("build.profile") == "release") {
        val buildBuildFile = File(projectDir, "src/main/resources/build.properties")
        if (buildBuildFile.exists()) {
            buildBuildFile.delete()
        } else {
            buildBuildFile.parentFile.mkdirs()
        }

        buildBuildFile.writer().use {
            val p = Properties()
            p["build_id"] = UUID.randomUUID().toString()
            p["build_date"] = Date().toInstant().toString()
            p["build_version"] = project.version.toString()
            p.store(it, null)
        }
    }
}
tasks["processResources"].dependsOn("createProperties")

configurations {
    create("empty")
}

nativeImage {
    dependsOn("shadowJar")
    setClasspath(File(project.buildDir, "libs/spp-cli-${project.version}.jar"))
    runtimeClasspath = configurations.getByName("empty")
    if (System.getenv("GRAALVM_HOME") != null) {
        graalVmHome = System.getenv("GRAALVM_HOME")
    }
    buildType { build ->
        build.executable(main = "spp.cli.Main")
    }
    executableName = "spp-cli"
    outputDirectory = file("$buildDir/graal")
    arguments("--no-fallback")
    arguments("-H:+ReportExceptionStackTraces")
}

tasks.getByName<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("spp-cli")
    archiveClassifier.set("")
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "spp.cli.Main"
            )
        )
    }
}
tasks.getByName("build").dependsOn("shadowJar")

configurations.runtimeClasspath {
    exclude("ch.qos.logback", "logback-classic")
}

tasks.getByName<Test>("test") {
    failFast = true
    useJUnitPlatform()
    if (System.getProperty("test.profile") != "integration") {
        exclude("integration/**")
    }

    testLogging {
        events("passed", "skipped", "failed")
        setExceptionFormat("full")

        outputs.upToDateWhen { false }
        showStandardStreams = true
    }
}

tasks {
    register("assembleUp") {
        dependsOn("assemble", "composeUp")
    }
    getByName("composeUp").mustRunAfter("assemble")
}

dockerCompose {
    dockerComposeWorkingDirectory.set(File("./e2e"))
    removeVolumes.set(true)
    waitForTcpPorts.set(false)
}

apollo {
    packageNamesFromFilePaths("spp.cli.protocol")
}

spotless {
    kotlin {
        targetExclude("**/generated/**")
        licenseHeaderFile(file("LICENSE-HEADER.txt"))
    }
}
