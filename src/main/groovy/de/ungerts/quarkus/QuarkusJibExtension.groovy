package de.ungerts.quarkus

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class QuarkusJibExtension {

    private final Property<String> libsDirPath
    private final Property<String> applicationLayersCachePath
    private final Property<String> baseImageLayersCachePath
    private final Property<String> baseImage
    private final Property<String> imageName

    QuarkusJibExtension(Project project) {
        def objectFactory = project.getObjects()
        libsDirPath = objectFactory.property(String.class)
        applicationLayersCachePath = objectFactory.property(String.class)
        baseImageLayersCachePath = objectFactory.property(String.class)
        baseImage = objectFactory.property(String.class)
        imageName = objectFactory.property(String.class)
        libsDirPath.set("${project.buildDir}${File.separator}lib")
        applicationLayersCachePath.set("${project.buildDir}${File.separator}jib-app-cache")
        baseImageLayersCachePath.set("${project.buildDir}${File.separator}jib-base-cache")
        baseImage.set('gcr.io/distroless/java:11')
        imageName.set('runner-image')
    }

    @Input
    @Optional
    String getLibsDirPath() {
        return libsDirPath.get()
    }

    @Input
    @Optional
    String getApplicationLayersCachePath() {
        return applicationLayersCachePath.get()
    }

    @Input
    @Optional
    String getBaseImageLayersCachePath() {
        return baseImageLayersCachePath.get()
    }

    @Input
    @Optional
    String getBaseImage() {
        return baseImage.get()
    }

    @Input
    @Optional
    String getImageName() {
        return imageName.get()
    }

    void setLibsDirPath(String libsDirPath) {
         this.libsDirPath.set(libsDirPath)
    }

    void setApplicationLayersCachePath(String applicationLayersCachePath) {
        this.applicationLayersCachePath.set(applicationLayersCachePath)
    }

    void setBaseImageLayersCachePath(String baseImageLayersCachePath) {
        this.baseImageLayersCachePath.set(baseImageLayersCachePath)
    }

    void setBaseImage(String baseImage) {
        this.baseImage.set(baseImage)
    }

    void setImageName(String imageName) {
        this.imageName.set(imageName)
    }

}
