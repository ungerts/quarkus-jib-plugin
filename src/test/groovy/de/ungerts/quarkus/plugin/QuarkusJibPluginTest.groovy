package de.ungerts.quarkus.plugin

import com.google.cloud.tools.jib.api.Containerizer
import com.google.cloud.tools.jib.api.Jib
import com.google.cloud.tools.jib.api.TarImage
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static org.gradle.testkit.runner.TaskOutcome.*

import java.nio.file.Files
import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.*

class QuarkusJibPluginTest {

    @Test
    void testJibImageTarBuild(@TempDir Path tempProjectRoot) {
        Path buildFile = tempProjectRoot.resolve('build.gradle')
        Path mocklib = tempProjectRoot.resolve("build${File.separator}lib${File.separator}mocklib.jar")
        Files.createDirectories(mocklib.getParent())
        Files.createFile(mocklib)
        Files.createFile(tempProjectRoot.resolve("build${File.separator}test-runner.jar"))

        InputStream buildFileContent =
                getClass().getClassLoader().getResourceAsStream('gradle/configuration/build.gradle')
        Files.copy(buildFileContent, buildFile)

        def scratchImagePath = tempProjectRoot.resolve("build${File.separator}jib-base-cache${File.separator}images${File.separator}scratch!latest")
        Files.createDirectories(scratchImagePath)

        InputStream scratchConfigContent =
                getClass().getClassLoader().getResourceAsStream('image/scratch/config.json')
        Files.copy(scratchConfigContent, scratchImagePath.resolve('config.json'))

        InputStream scratchManifestContent =
                getClass().getClassLoader().getResourceAsStream('image/scratch/manifest.json')
        Files.copy(scratchManifestContent, scratchImagePath.resolve('manifest.json'))

        def result = GradleRunner.create()
                .withProjectDir(tempProjectRoot.toFile())
                .withPluginClasspath()
                .withGradleVersion('6.0.1')
                .withArguments('jibImageTar')
                .build()
        println "Output: ${System.lineSeparator()}${result.output}"
        org.junit.jupiter.api.Assertions.assertTrue(result.task(':jibImageTar').outcome == org.gradle.testkit.runner.TaskOutcome.SUCCESS)
        Path imagePath = tempProjectRoot.resolve("build${File.separator}runner-image.tar")
        org.junit.jupiter.api.Assertions.assertTrue(Files.isRegularFile(imagePath))
    }


}
