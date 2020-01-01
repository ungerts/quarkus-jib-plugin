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

}
