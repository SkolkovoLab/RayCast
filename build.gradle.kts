plugins {
    id("java-library")
}

version = "dev"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation("net.minestom:minestom-snapshots:fed512eaf6")
    implementation("org.apache.commons:commons-geometry-euclidean:1.0")

    compileOnly("org.jetbrains:annotations:24.1.0")
}
