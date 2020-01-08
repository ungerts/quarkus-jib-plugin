package de.ungerts.quarkus.plugin

import de.ungerts.quarkus.config.QuarkusJibExtension
import de.ungerts.quarkus.tasks.JibDockerBuild
import de.ungerts.quarkus.tasks.JibImageTar
import de.ungerts.quarkus.tasks.JibRegistryBuild
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
            quarkusBuild.each {task1 -> task1.logger.info "Task name: ${task1.name}"
            }
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
        project.getTasks().create('jibRegistryBuild', JibRegistryBuild.class, { task ->
            task.setGroup("Quarkus-Jib")
            task.setDescription("Builds a container image to a Docker registry.")
            task.setQuarkusJibExtension(extension)
            def quarkusBuild = project.getTasksByName('quarkusBuild', true)
            if (quarkusBuild) {
                task.dependsOn(quarkusBuild)
            }
        })
    }
}
