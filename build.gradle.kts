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

val targetJavaVersion = 11
tasks.compileJava {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release = targetJavaVersion
    }
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
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