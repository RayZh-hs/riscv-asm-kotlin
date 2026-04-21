plugins {
    kotlin("jvm") version "1.9.24"
}

group = "space.norb"
version = "0.1.0"

kotlin {
    jvmToolchain(8)
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}
