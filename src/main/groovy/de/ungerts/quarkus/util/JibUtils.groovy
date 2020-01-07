package de.ungerts.quarkus.util

import com.google.cloud.tools.jib.api.AbsoluteUnixPath
import com.google.cloud.tools.jib.api.Containerizer
import com.google.cloud.tools.jib.api.DockerDaemonImage
import com.google.cloud.tools.jib.api.ImageReference
import com.google.cloud.tools.jib.api.Jib
import com.google.cloud.tools.jib.api.JibContainer
import com.google.cloud.tools.jib.api.JibContainerBuilder
import com.google.cloud.tools.jib.api.LayerConfiguration
import com.google.cloud.tools.jib.api.LogEvent
import com.google.cloud.tools.jib.api.Port
import com.google.cloud.tools.jib.api.RegistryImage
import com.google.cloud.tools.jib.api.TarImage
import com.google.cloud.tools.jib.frontend.CredentialRetrieverFactory
import de.ungerts.quarkus.config.QuarkusJibExtension
import org.gradle.api.Project

import java.nio.file.Paths
import java.time.Instant

class JibUtils {

    static void buildToDocker(QuarkusJibExtension quarkusJibExtension, Project project) {
        def containerizer = Containerizer.to(DockerDaemonImage
                .named(quarkusJibExtension.to.imageName))
                .addEventHandler(LogEvent.class, { event ->
                    println event.getMessage()
                })
                .setOfflineMode(quarkusJibExtension.offlineMode)
                .setBaseImageLayersCache(Paths.get(quarkusJibExtension.baseImageLayersCachePath))
                .setApplicationLayersCache(Paths.get(quarkusJibExtension.applicationLayersCachePath))
        buildImage(project, quarkusJibExtension, containerizer)
    }

    static void buildToImageTar(QuarkusJibExtension quarkusJibExtension, Project project) {
        def tarPath = Paths.get("${project.buildDir}${File.separator}${quarkusJibExtension.tarFileName}")
        def containerizer = Containerizer.to(TarImage
                .at(tarPath).named(quarkusJibExtension.to.imageName))
                .addEventHandler(LogEvent.class, { event ->
                    println event.getMessage()
                })
                .setOfflineMode(quarkusJibExtension.offlineMode)
                .setBaseImageLayersCache(Paths.get(quarkusJibExtension.baseImageLayersCachePath))
                .setApplicationLayersCache(Paths.get(quarkusJibExtension.applicationLayersCachePath))
        def container = buildImage(project, quarkusJibExtension, containerizer)
        writeImageDigest(container, "${project.buildDir}${File.separator}")
    }

    static void writeImageDigest(JibContainer container, String fileNameBase) {
        def digestFile = new File("${fileNameBase}digest.txt")
        digestFile.text = container.digest.toString()
        def imageIdFile = new File("${fileNameBase}image-id.txt")
        imageIdFile.text = container.digest.toString()
    }

    static void buildToRegistry(QuarkusJibExtension quarkusJibExtension, Project project) {
        def toRef = ImageReference.parse(quarkusJibExtension.to.imageName)
        def regImageto = RegistryImage.named(toRef)
        if (quarkusJibExtension.to.credentialHelper) {
            CredentialRetrieverFactory toCredentialFactory = CredentialRetrieverFactory.forImage(toRef, { event ->
                println event.getMessage()
            })
            def toRetriever = toCredentialFactory.dockerCredentialHelper(quarkusJibExtension.to.credentialHelper)
            regImageto.addCredentialRetriever(toRetriever)
        }

        def containerizer = Containerizer.to(regImageto)
                .addEventHandler(LogEvent.class, { event ->
                    println event.getMessage()
                })
                .setOfflineMode(quarkusJibExtension.offlineMode)
                .setAllowInsecureRegistries(quarkusJibExtension.allowInsecureRegistries)
                .setBaseImageLayersCache(Paths.get(quarkusJibExtension.baseImageLayersCachePath))
                .setApplicationLayersCache(Paths.get(quarkusJibExtension.applicationLayersCachePath))
        buildImage(project, quarkusJibExtension, containerizer)
    }

    private static JibContainer buildImage(Project project, QuarkusJibExtension quarkusJibExtension, Containerizer containerizer) {
        def runnerFilePath = project.fileTree(dir: "${project.buildDir}", include: '*-runner.jar').getFiles()[0].toPath()
        def runnerLayer = LayerConfiguration.builder()
                .addEntry(runnerFilePath, AbsoluteUnixPath.get('/app/app.jar'))
                .build()
        def port = Port.tcp(quarkusJibExtension.exposedPort)
        return createContainerBuilder(quarkusJibExtension)
                .setWorkingDirectory(AbsoluteUnixPath.get('/app'))
                .addLayer([Paths.get(quarkusJibExtension.libsDirPath)], '/app')
                .addLayer(runnerLayer)
                .addExposedPort(port)
                .setEntrypoint(['java', '-jar', 'app.jar'])
                .setCreationTime(Instant.now())
                .containerize(containerizer)
    }

    private static JibContainerBuilder createContainerBuilder(QuarkusJibExtension quarkusJibExtension) {
        JibContainerBuilder builder
        def fromRef = ImageReference.parse(quarkusJibExtension.from.baseImage)
        if (fromRef.registry) {
            def regImageFrom = RegistryImage.named(fromRef)
            if (quarkusJibExtension.from.credentialHelper) {
                CredentialRetrieverFactory fromCredentialFactory = CredentialRetrieverFactory
                        .forImage(fromRef, { event ->
                            println event.getMessage()
                        })
                def fromRetiever = fromCredentialFactory.dockerCredentialHelper(quarkusJibExtension.from.credentialHelper)
                regImageFrom.addCredentialRetriever(fromRetiever)
            }
            builder = Jib.from(regImageFrom)
        } else {
            builder = Jib.from(fromRef)
        }
        builder
    }


}
