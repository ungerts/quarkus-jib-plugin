package de.ungerts.quarkus.util

import com.google.cloud.tools.jib.api.AbsoluteUnixPath
import com.google.cloud.tools.jib.api.Containerizer
import com.google.cloud.tools.jib.api.Credential
import com.google.cloud.tools.jib.api.DockerDaemonImage
import com.google.cloud.tools.jib.api.ImageReference
import com.google.cloud.tools.jib.api.Jib
import com.google.cloud.tools.jib.api.JibContainerBuilder
import com.google.cloud.tools.jib.api.JibContainer
import com.google.cloud.tools.jib.api.LayerConfiguration
import com.google.cloud.tools.jib.api.LogEvent
import com.google.cloud.tools.jib.api.Port
import com.google.cloud.tools.jib.api.RegistryImage
import com.google.cloud.tools.jib.api.TarImage
import com.google.cloud.tools.jib.frontend.CredentialRetrieverFactory
import com.google.cloud.tools.jib.registry.credentials.CredentialRetrievalException
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
        def container = buildImage(project, quarkusJibExtension, containerizer)
        writeImageDigest(container, "${project.buildDir}", 'docker')
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
        writeImageDigest(container, "${project.buildDir}", 'tar')
    }

    static void writeImageDigest(JibContainer container, String baseDir, String prefix) {
        def digestFile = new File("${baseDir}${File.separator}${prefix}-digest.txt")
        digestFile.text = container.digest.toString()
        def imageIdFile = new File("${baseDir}${File.separator}${prefix}-image-id.txt")
        imageIdFile.text = container.imageId.toString()
    }

    static void buildToRegistry(QuarkusJibExtension quarkusJibExtension, Project project) {
        def toRef = ImageReference.parse(quarkusJibExtension.to.imageName)
        RegistryImage regImageto = createRegistryImage(toRef, quarkusJibExtension.to.credentialHelper, quarkusJibExtension.to.username, quarkusJibExtension.to.password)

        def containerizer = Containerizer.to(regImageto)
                .addEventHandler(LogEvent.class, { event ->
                    println event.getMessage()
                })
                .setOfflineMode(quarkusJibExtension.offlineMode)
                .setAllowInsecureRegistries(quarkusJibExtension.allowInsecureRegistries)
                .setBaseImageLayersCache(Paths.get(quarkusJibExtension.baseImageLayersCachePath))
                .setApplicationLayersCache(Paths.get(quarkusJibExtension.applicationLayersCachePath))
        def container = buildImage(project, quarkusJibExtension, containerizer)
        writeImageDigest(container, "${project.buildDir}", 'registry')
    }

    private static RegistryImage createRegistryImage(ImageReference imageRef, String credentialHelper, String username, String password) {
        def regImage = RegistryImage.named(imageRef)
        if (credentialHelper) {
            if (credentialHelper == 'openshift') {
                regImage.addCredentialRetriever({
                    try {
                        return createOpenshiftCredential()
                    } catch (RuntimeException e) {
                        throw new CredentialRetrievalException(e)
                    }
                })
            } else {
                CredentialRetrieverFactory retrieverFactory = CredentialRetrieverFactory.forImage(imageRef, { event ->
                    println event.getMessage()
                })
                def retriever = retrieverFactory.dockerCredentialHelper(credentialHelper)
                regImage.addCredentialRetriever(retriever)
            }
        } else {
            regImage.addCredential(username, password)
        }
        regImage
    }

    private static Optional<Credential> createOpenshiftCredential() {
        def  token = new File('/var/run/secrets/kubernetes.io/serviceaccount/token').text
        Optional.of(Credential.from('openshift', token))
    }

    private static JibContainer buildImage(Project project, QuarkusJibExtension quarkusJibExtension, Containerizer containerizer) {
        def runnerFilePath = project.fileTree(dir: "${project.buildDir}", include: '*-runner.jar').getSingleFile().toPath()
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
            def regImageFrom = createRegistryImage(fromRef, quarkusJibExtension.from.credentialHelper, quarkusJibExtension.from.username, quarkusJibExtension.from.password)
            builder = Jib.from(regImageFrom)
        } else {
            builder = Jib.from(fromRef)
        }
        builder
    }


}
