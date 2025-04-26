plugins {
    id("java-library")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

version = "dev"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("net.minestom:minestom-snapshots:0366b58bfe")
    testImplementation("net.minestom:minestom-snapshots:0366b58bfe")
    implementation("org.apache.commons:commons-geometry-euclidean:1.0")
    compileOnly("org.jetbrains:annotations:24.1.0")
}
