/*
 * Copyright 2021 Louis Cognault Ayeva Derman. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    `maven-publish`
    signing
}

signing {
    useInMemoryPgpKeys(
        propertyOrEnvOrNull("GPG_key_id"),
        propertyOrEnvOrNull("GPG_private_key") ?: return@signing,
        propertyOrEnv("GPG_private_password")
    )
    sign(publishing.publications)
}

publishing {
    project.group = "com.louiscad.splitties"
    project.version = project.thisLibraryVersion

    val mavenPublications = publications.withType<MavenPublication>()
    mavenPublications.all {
        artifact(project.tasks.emptyJavadocJar())
        setupPom()
    }
    mavenPublications.findByName("kotlinMultiplatform")?.let {
        val prefix = if (project.isFunPack) "splitties-fun-pack" else "splitties"
        project.afterEvaluate {
            it.artifactId = "$prefix-${project.name}"
        }
    }

    mavenCentralStagingPublishing(
        project = project,
        repositoryId = System.getenv("sonatype_staging_repo_id")
    )
    sonatypeSnapshotsPublishing(project = project)
}
