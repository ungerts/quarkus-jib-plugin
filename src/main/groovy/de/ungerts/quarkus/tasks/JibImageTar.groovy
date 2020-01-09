package de.ungerts.quarkus.tasks

import de.ungerts.quarkus.config.QuarkusJibExtension
import de.ungerts.quarkus.util.JibUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction

import javax.annotation.Nullable
import java.nio.file.Paths

class JibImageTar extends DefaultTask {

    @Nullable
    private QuarkusJibExtension quarkusJibExtension

    @TaskAction
    void buildImage() {
        JibUtils.buildToImageTar(quarkusJibExtension, project)
    }

    @Nested
    @Nullable
    QuarkusJibExtension getQuarkusJib() {
        return quarkusJibExtension
    }

    void setQuarkusJibExtension(QuarkusJibExtension quarkusJibExtension) {
        this.quarkusJibExtension = quarkusJibExtension
    }

    @InputFiles
    FileCollection getInputFiles() {
        FileCollection runnerCollection = project.fileTree(dir: "${project.buildDir}", include: '*-runner.jar')
        FileCollection libCollection = project.fileTree(dir: "${project.buildDir}${File.separator}lib", include: '*')
        List<FileCollection> inputCollection = [runnerCollection, libCollection]
        String baseDir = "${project.buildDir}${File.separator}"
        [Paths.get("${project.buildDir}${File.separator}${quarkusJibExtension.tarFileName}").toFile(),
         Paths.get("${baseDir}tar-digest.txt").toFile(),
         Paths.get("${baseDir}tar-image-id.txt").toFile()]
                .findAll { it.exists() }
                .each { inputCollection.add(project.files(it)) }
        if (logger.isEnabled(LogLevel.DEBUG)) {
            project.files(inputCollection).each { file -> logger.debug("Input file found: ${file.toString()}") }
        }
        project.files(inputCollection)
    }

    @OutputFiles
    FileCollection getOutputFiles() {
        if (logger.isEnabled(LogLevel.DEBUG)) {
            project.files(Paths.get("${project.buildDir}${File.separator}${quarkusJibExtension.tarFileName}").toFile())
                    .each { file -> logger.debug("Output file found: ${file.toString()}") }
        }
        project.files(Paths.get("${project.buildDir}${File.separator}${quarkusJibExtension.tarFileName}").toFile())
    }

}
