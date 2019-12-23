package de.ungerts.quarkus.config

import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

import javax.annotation.Nullable
import javax.inject.Inject

class FromImage {

    private String baseImage

    @Nullable
    private String credentialHelper

    @Inject
    FromImage(ObjectFactory objectFactory) {
        baseImage = 'gcr.io/distroless/java:11'
    }

    @Input
    @Optional
    String getBaseImage() {
        return baseImage
    }

    void setBaseImage(String baseImage) {
        this.baseImage = baseImage
    }

    @Input
    @Optional
    @Nullable
    String getCredentialHelper() {
        return credentialHelper
    }

    void setCredentialHelper(@Nullable String credentialHelper) {
        this.credentialHelper = credentialHelper
    }
}
