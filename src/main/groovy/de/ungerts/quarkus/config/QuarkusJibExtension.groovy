package de.ungerts.quarkus.config

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

class QuarkusJibExtension {

    private final Property<String> libsDirPath
    private final Property<String> applicationLayersCachePath
    private final Property<String> baseImageLayersCachePath
    private final Property<Integer> exposedPort
    private final Property<Boolean> offlineMode
    private final FromImage from
    private final ToImage to

    QuarkusJibExtension(Project project) {
        def objectFactory = project.getObjects()
        libsDirPath = objectFactory.property(String.class)
        applicationLayersCachePath = objectFactory.property(String.class)
        baseImageLayersCachePath = objectFactory.property(String.class)
        exposedPort = objectFactory.property(Integer.class)
        offlineMode = objectFactory.property(Boolean.class)
        from = objectFactory.newInstance(FromImage.class)
        to = objectFactory.newInstance(ToImage.class)
        libsDirPath.set("${project.buildDir}${File.separator}lib")
        applicationLayersCachePath.set("${project.buildDir}${File.separator}jib-app-cache")
        baseImageLayersCachePath.set("${project.buildDir}${File.separator}jib-base-cache")
        exposedPort.set(8080)
        offlineMode.set(false)

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

    void setLibsDirPath(String libsDirPath) {
         this.libsDirPath.set(libsDirPath)
    }

    void setApplicationLayersCachePath(String applicationLayersCachePath) {
        this.applicationLayersCachePath.set(applicationLayersCachePath)
    }

    void setBaseImageLayersCachePath(String baseImageLayersCachePath) {
        this.baseImageLayersCachePath.set(baseImageLayersCachePath)
    }

    @Input
    @Optional
    Integer getExposedPort() {
        return this.exposedPort.get()
    }

    void setExposedPort(Integer exposedPort) {
        this.exposedPort.set(exposedPort)
    }

    @Input
    @Optional
    Boolean getOfflineMode() {
        return offlineMode.get()
    }

    void setOfflineMode(Boolean offlineMode) {
        this.offlineMode.set(offlineMode)
    }

    void from(Action<? super FromImage> action) {
        action.execute(from)
    }

    @Nested
    @Optional
    FromImage getFrom() {
        return from
    }

    void to(Action<? super ToImage> action) {
        action.execute(to)
    }

    @Nested
    @Optional
    ToImage getTo() {
        return to
    }
}
