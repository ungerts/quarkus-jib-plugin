package de.ungerts.quarkus.util

import com.google.cloud.tools.jib.api.AbsoluteUnixPath
import com.google.cloud.tools.jib.api.Containerizer
import com.google.cloud.tools.jib.api.DockerDaemonImage
import com.google.cloud.tools.jib.api.Jib
import com.google.cloud.tools.jib.api.LayerConfiguration
import com.google.cloud.tools.jib.api.LogEvent
import com.google.cloud.tools.jib.api.Port
import com.google.cloud.tools.jib.api.TarImage
import de.ungerts.quarkus.config.QuarkusJibExtension
import org.gradle.api.Project

import java.nio.file.Paths

class JibUtils {

    static void buildToDocker(QuarkusJibExtension quarkusJibExtension, Project project) {
        def containerizer = Containerizer.to(DockerDaemonImage
                .named(quarkusJibExtension.imageName))
                .addEventHandler(LogEvent.class, { event ->
                    println event.getMessage()
                })
                .setBaseImageLayersCache(Paths.get(quarkusJibExtension.baseImageLayersCachePath))
                .setApplicationLayersCache(Paths.get(quarkusJibExtension.applicationLayersCachePath))
        buildImage(project, quarkusJibExtension, containerizer)
    }

    static void buildToImageTar(QuarkusJibExtension quarkusJibExtension, Project project) {
        def tarPath = Paths.get("${project.buildDir}${File.separator}${quarkusJibExtension.imageName}.tar")
        def containerizer = Containerizer.to(TarImage
                .at(tarPath).named(quarkusJibExtension.imageName))
                .addEventHandler(LogEvent.class, { event ->
                    println event.getMessage()
                })
                .setBaseImageLayersCache(Paths.get(quarkusJibExtension.baseImageLayersCachePath))
                .setApplicationLayersCache(Paths.get(quarkusJibExtension.applicationLayersCachePath))
        buildImage(project, quarkusJibExtension, containerizer)
    }

    private static void buildImage(Project project, QuarkusJibExtension quarkusJibExtension, Containerizer containerizer) {
        def runnerFilePath = project.fileTree(dir: "${project.buildDir}", include: '*-runner.jar').getFiles()[0].toPath()
        def runnerLayer = LayerConfiguration.builder()
                .addEntry(runnerFilePath, AbsoluteUnixPath.get('/app/app.jar'))
                .build()
        def port = Port.tcp(quarkusJibExtension.exposedPort)
        Jib.from(quarkusJibExtension.baseImage)
                .setWorkingDirectory(AbsoluteUnixPath.get('/app'))
                .addLayer([Paths.get(quarkusJibExtension.libsDirPath)], '/app')
                .addLayer(runnerLayer)
                .addExposedPort(port)
                .setEntrypoint(['java', '-jar', 'app.jar'])
                .containerize(containerizer)
    }


}
