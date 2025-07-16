plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

group = "me.memencio.libbyfabric"
version = "1.0.0-SNAPSHOT"

repositories {
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    maven { url = uri("https://repo.kyngs.xyz/public/") }
    mavenCentral()
}

dependencies {
    compileOnly("net.fabricmc:fabric-loader:${properties["loader_version"]}")
    compileOnly("org.slf4j:slf4j-api:1.8.0-beta4")
    implementation("xyz.kyngs.libby:libby-core:1.7.0")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    archiveClassifier.set("")
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            from(components["shadow"])
        }
    }
}