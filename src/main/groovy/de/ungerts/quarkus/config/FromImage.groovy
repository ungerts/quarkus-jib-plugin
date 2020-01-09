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

    @Nullable
    private String username

    @Nullable
    private String password

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

    @Input
    @Optional
    @Nullable
    String getUsername() {
        return username
    }

    void setUsername(@Nullable String username) {
        this.username = username
    }

    @Input
    @Optional
    @Nullable
    String getPassword() {
        return password
    }

    void setPassword(@Nullable String password) {
        this.password = password
    }
}
