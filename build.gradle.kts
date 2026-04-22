import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.registering
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.24"
    `maven-publish`
    signing

    id("com.gradleup.nmcp.aggregation") version "1.2.1"
}

group = "space.norb"
version = "0.1.0"

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = "riscv-asm-kotlin"
            version = project.version.toString()

            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            pom {
                name.set("riscv-asm-kotlin")
                description.set("Small Kotlin DSL for generating RISC-V assembly text.")
                url.set("https://github.com/RayZh-hs/riscv-asm-kotlin")

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("http://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("RayZh-hs")
                        name.set("RayZh")
                        email.set("ray_zh@sjtu.edu.cn")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/RayZh-hs/riscv-asm-kotlin.git")
                    developerConnection.set("scm:git:ssh://github.com/RayZh-hs/riscv-asm-kotlin.git")
                    url.set("https://github.com/RayZh-hs/riscv-asm-kotlin.git")
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

nmcpAggregation {
    centralPortal {
        username = System.getenv("MAVEN_CENTRAL_USERNAME")
        password = System.getenv("MAVEN_CENTRAL_PASSWORD")
        publishingType = "AUTOMATIC"
    }

    publishAllProjectsProbablyBreakingProjectIsolation()
}

tasks.withType<Sign>().configureEach {
    onlyIf {
        gradle.taskGraph.allTasks.any { it.name == "publishAggregationToCentralPortal" }
    }
}
