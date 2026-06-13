@file:Suppress("UnstableApiUsage")

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository

plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "org.jfxcore"
version = project.findProperty("TAG_VERSION_PROJECT") ?: "1.0-SNAPSHOT"

val signingKey: String? by project
val signingKeyName: String? by project
val signingPassword: String? by project
val repositoryUserName: String? by project
val repositoryPassword: String? by project

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
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer ${Base64.getEncoder().encodeToString(
                    "$repositoryUserName:$repositoryPassword".toByteArray(Charsets.UTF_8))}")
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
                description.set("FXML/2 markup support library")
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
            if (project.hasProperty("REPOSITORY_URL")) {
                credentials {
                    username = repositoryUserName
                    password = repositoryPassword
                }
                url = uri(project.property("REPOSITORY_URL") as String)
            }
        }
    }
}

signing {
    setRequired {
        gradle.taskGraph.allTasks.any { it is PublishToMavenRepository }
    }

    useInMemoryPgpKeys(signingKeyName, signingKey, signingPassword)
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
