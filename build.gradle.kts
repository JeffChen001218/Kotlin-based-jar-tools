plugins {
    kotlin("jvm") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

tasks.jar.configure {
    manifest {
        attributes(mapOf("Main-Class" to "org.example.MainKt"))
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.apache.commons:commons-imaging:1.0.0-alpha5")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}