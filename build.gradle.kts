plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktechMavenPublish)
}

group = "fi.evident.skema"
description = "Schema definition DSL for Kotlin"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaJavadoc"))
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(
        groupId = "fi.evident.skema",
        artifactId = "skema",
        version = project.findProperty("projectVersion") as String? ?: "0.1.0-SNAPSHOT"
    )

    pom {
        name = "skema"
        description = provider { project.description }
        url = "https://github.com/EvidentSolutions/skema"
        inceptionYear = "2025"

        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        developers {
            developer {
                id = "komu"
                name = "Juha Komulainen"
                url = "https://github.com/komu"
            }
        }

        scm {
            url = "https://github.com/EvidentSolutions/skema"
            connection = "scm:git:https://github.com/EvidentSolutions/skema.git"
            developerConnection = "scm:git:git@github.com:EvidentSolutions/skema.git"
        }

        issueManagement {
            system = "GitHub"
            url = "https://github.com/EvidentSolutions/skema/issues"
        }
    }
}
