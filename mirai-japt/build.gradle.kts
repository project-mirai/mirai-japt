import java.util.*

plugins {
    kotlin("jvm")
    java
    id("com.jfrog.bintray")
    `maven-publish`
}

val kotlinVersion: String by rootProject.ext
val atomicFuVersion: String by rootProject.ext
val coroutinesVersion: String by rootProject.ext
val kotlinXIoVersion: String by rootProject.ext
val coroutinesIoVersion: String by rootProject.ext
val serializationVersion: String by rootProject.ext

val klockVersion: String by rootProject.ext
val ktorVersion: String by rootProject.ext

description = "Java helper for Mirai"

@Suppress("PropertyName")
val mirai_japt_version: String by rootProject.ext
version = mirai_japt_version

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    sourceSets {
        all {
            languageSettings.enableLanguageFeature("InlineClasses")

            languageSettings.useExperimentalAnnotation("kotlin.Experimental")
        }
    }
}

fun kotlinx(id: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$id:$version"

dependencies {
    implementation("net.mamoe:mirai-core-jvm:0.22.0")

    api(kotlinx("coroutines-core", coroutinesVersion))
    api(kotlin("stdlib-jdk8", kotlinVersion))
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

bintray {
    val keyProps = Properties()
    val keyFile = file("../keys.properties")
    if (keyFile.exists()) keyFile.inputStream().use { keyProps.load(it) }

    user = keyProps.getProperty("bintrayUser")
    key = keyProps.getProperty("bintrayKey")
    setPublications("mavenJava")
    setConfigurations("archives")

    pkg.apply {
        repo = "mirai"
        name = "mirai-japt"
        setLicenses("AGPLv3")
        publicDownloadNumbers = true
        vcsUrl = "https://github.com/mamoe/mirai"
    }
}

@Suppress("DEPRECATION")
val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            groupId = "net.mamoe"
            artifactId = "mirai-japt"
            version = mirai_japt_version

            pom.withXml {
                val root = asNode()
                root.appendNode("description", description)
                root.appendNode("name", project.name)
                root.appendNode("url", "https://github.com/mamoe/mirai")
                root.children().last()
            }

            artifact(sourcesJar.get())
        }
    }
}