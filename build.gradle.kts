@file:Suppress("UnstableApiUsage")

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "org.jfxcore"
version = project.findProperty("TAG_VERSION_PROJECT") ?: "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).links("https://openjfx.io/javadoc/17/")
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

if (!version.toString().endsWith("-SNAPSHOT")) {
    val mavenCentralFixup by tasks.registering {
        doLast {
            val url = project.property("REPOSITORY_POST_URL") as String
            val username = project.property("REPOSITORY_USERNAME") as String
            val password = project.property("REPOSITORY_PASSWORD") as String
            val userToken = Base64.getEncoder().encodeToString("$username:$password".toByteArray(Charsets.UTF_8))
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer $userToken")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build()

            logger.info("POST $url")

            HttpClient.newHttpClient().use {
                val response = it.send(request, HttpResponse.BodyHandlers.ofString())
                logger.info("Received status code: ${response.statusCode()}")
            }
        }
    }

    tasks.publish {
        finalizedBy(mavenCentralFixup)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.jar)
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                url.set("https://github.com/jfxcore/markup")
                name.set("markup")
                description.set("FXML 2.0 markup support library")
                licenses {
                    license {
                        name.set("BSD-3-Clause")
                        url.set("https://opensource.org/licenses/BSD-3-Clause")
                    }
                }
                developers {
                    developer {
                        id.set("jfxcore")
                        name.set("JFXcore")
                        organization.set("JFXcore")
                        organizationUrl.set("https://github.com/jfxcore")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/jfxcore/markup.git")
                    developerConnection.set("scm:git:https://github.com/jfxcore/markup.git")
                    url.set("https://github.com/jfxcore/markup")
                }
            }
        }
    }
    repositories {
        maven {
            if (project.hasProperty("REPOSITORY_USERNAME")
                    && project.hasProperty("REPOSITORY_PASSWORD")
                    && project.hasProperty("REPOSITORY_URL")) {
                credentials {
                    username = project.property("REPOSITORY_USERNAME") as String
                    password = project.property("REPOSITORY_PASSWORD") as String
                }
                url = uri(project.property("REPOSITORY_URL") as String)
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

dependencies {
    compileOnly("org.openjfx:javafx-base:23:linux")
    compileOnly("org.openjfx:javafx-graphics:23:linux")
    compileOnly("org.openjfx:javafx-controls:23:linux")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation("org.openjfx:javafx-base:23:linux")
    testImplementation("org.openjfx:javafx-graphics:23:linux")
}
