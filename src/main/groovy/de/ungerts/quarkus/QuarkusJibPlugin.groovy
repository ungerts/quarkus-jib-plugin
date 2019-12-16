package de.ungerts.quarkus

import org.gradle.api.Plugin
import org.gradle.api.Project

class QuarkusJibPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        final def extension = project.extensions.create('quarkusJib', QuarkusJibExtension.class, project)
        project.getTasks().create('jibImageTar', JibImageTar.class, { task ->
            task.setGroup("Quarkus-Jib")
            task.setDescription("Builds a container image to a tarball.")
            task.setQuarkusJibExtension(extension)
            def quarkusBuild = project.getTasksByName('quarkusBuild', true)
            if (quarkusBuild) {
                task.dependsOn(quarkusBuild)
            }
        })
        project.getTasks().create('jibDockerBuild', JibDockerBuild.class, { task ->
            task.setGroup("Quarkus-Jib")
            task.setDescription("Builds a container image to a Docker daemon.")
            task.setQuarkusJibExtension(extension)
            def quarkusBuild = project.getTasksByName('quarkusBuild', true)
            if (quarkusBuild) {
                task.dependsOn(quarkusBuild)
            }
        })
    }
}
