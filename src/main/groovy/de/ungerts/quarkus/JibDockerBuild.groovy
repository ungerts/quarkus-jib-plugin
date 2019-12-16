package de.ungerts.quarkus

import com.google.cloud.tools.jib.api.AbsoluteUnixPath
import com.google.cloud.tools.jib.api.Containerizer
import com.google.cloud.tools.jib.api.DockerDaemonImage
import com.google.cloud.tools.jib.api.Jib
import com.google.cloud.tools.jib.api.LayerConfiguration
import com.google.cloud.tools.jib.api.LogEvent
import com.google.cloud.tools.jib.api.TarImage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

class JibDockerBuild extends DefaultTask {

    QuarkusJibExtension quarkusJibExtension

    @TaskAction
    void buildImage() {
        def runnerFilePath = project.fileTree(dir: "${project.buildDir}", include: '*-runner.jar').getFiles()[0].toPath()
        def runnerLayer = LayerConfiguration.builder()
                .addEntry(runnerFilePath, AbsoluteUnixPath.get('/app/app.jar'))
                .build()
        Jib.from(quarkusJibExtension.baseImage)
                .setWorkingDirectory(AbsoluteUnixPath.get('/app'))
                .addLayer([Paths.get(quarkusJibExtension.libsDirPath)], '/app')
                .addLayer(runnerLayer)
                .setEntrypoint(['java', '-jar', 'app.jar'])
                .containerize(Containerizer.to(DockerDaemonImage
                        .named(quarkusJibExtension.imageName))
                        .addEventHandler(LogEvent.class, { event ->
                            println event.getMessage()
                        })
                        .setBaseImageLayersCache(Paths.get(quarkusJibExtension.baseImageLayersCachePath))
                        .setApplicationLayersCache(Paths.get(quarkusJibExtension.applicationLayersCachePath)))

    }

}
