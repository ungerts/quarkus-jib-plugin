package de.ungerts.quarkus.config

import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

import javax.annotation.Nullable
import javax.inject.Inject

class ToImage {

    private String imageName

    @Nullable
    private String credentialHelper

    @Nullable
    private String username

    @Nullable
    private String password

    @Inject
    ToImage(ObjectFactory objectFactory) {
        imageName = 'runner-image'
    }

    @Input
    @Optional
    String getImageName() {
        return imageName
    }

    void setImageName(String imageName) {
        this.imageName = imageName
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
