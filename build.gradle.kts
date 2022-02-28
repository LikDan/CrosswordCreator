var fileName = "Crossword"

plugins {
    java
    application
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.0"
    id("org.openjfx.javafxplugin") version "0.0.7"
    id("edu.sc.seis.launch4j") version "2.4.6"
}

group = "com.likco"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("it.sauronsoftware:junique:1.0.4")

    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
}

javafx {
    modules = mutableListOf("javafx.controls", "javafx.fxml")
    version = "15.0.1"
}

tasks.test {
    useJUnitPlatform()
}

launch4j {
    jreMinVersion = "1.0.0"
    headerType = "gui"
    mainClassName = "com.example.crossword.HelloApplicationKt"
    outfile = "$fileName.exe"
    jar = "../libs/$fileName.jar"
}

tasks.compileJava {
    options.isIncremental = true
    options.isFork = true
    options.isFailOnError = false

    options.release.set(8)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(sourceSets.main.get().output)
    archiveName = "$fileName.jar"
    dependsOn(configurations.runtimeClasspath)
    manifest {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.MF, META-INF/LICENSE")
        attributes(
            "Implementation-Title" to "ServiceHelper",
            "Implementation-Version" to archiveVersion,
            "Main-Class" to "com.example.crossword.HelloApplicationKt",
            "Class-Path" to configurations.runtimeClasspath.files.joinToString(" ") { "lib/$it.name" }
        )
    }
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}