# quarkus-jib-plugin

![](https://github.com/ungerts/quarkus-jib-plugin/workflows/build/badge.svg)

## Configuration

Plugin can be configured in ```build.gradle``` and ```settings.gradle```.
Variable ```quarkusJibPluginVersion``` should be configured in ```build.properties```.

```build.gradle```:

```groovy
plugins {
    id 'java'
    id 'io.quarkus'
    id 'de.ungerts.quarkus-jib'
}

apply plugin: 'io.quarkus'
apply plugin: 'de.ungerts.quarkus-jib'

quarkusJib {
    offlineMode = false
    from {
        baseImage = 'gcr.io/distroless/java:11'
    }
    to {
        imageName = "${project.name}"
    }
}
```

```settings.gradle```:

```groovy
pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == 'de.ungerts' && requested.id.name == 'quarkus-jib') {
                useModule("de.ungerts:quarkus-jib-plugin:${quarkusJibPluginVersion}")
            }
        }
    }
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id 'io.quarkus' version "${quarkusPluginVersion}"
    }
}

rootProject.name='quarkus-jib-example'
```

Parameters:

Parameter | Default | Description 
--- | --- | --- 
`libsDirPath` | `"${project.buildDir}${File.separator}lib"` | 
`applicationLayersCachePath` | `"${project.buildDir}${File.separator}jib-app-cache"` | 
`baseImageLayersCachePath` | `"${project.buildDir}${File.separator}jib-base-cache"` | 
`from` |  | Closure
`to` |  | Closure
`exposedPort` | `8080` | 
`offlineMode` | `false` | 
`tarFileName` | `'runner-image.tar'` | 
`allowInsecureRegistries` | `false` | 

To Specification:

Parameter | Default | Description 
--- | --- | --- 
`imageName` | `'runner-image'` | 
`credentialHelper` |  |  

From Specification:

Parameter | Default | Description 
--- | --- | --- 
`baseImage` | `'gcr.io/distroless/java:11'` | 
`credentialHelper` |  | 


## Example

### Requirements

```JAVA_HOME``` must to point to a JDK 11.

### Run example

Check out  the repo and install the plugin by executing Gradle in the projects' root directory.

On Windows:

```bat
gradlew install
```

On Linux/Mac:

```bat
./gradlew install
```

Change directory to ```examples/quarkus-jib```.

Build container image and write the image to a tar file.

On Windows:

```bat
gradlew jibImageTar
```

On Linux/Mac:

```bat
./gradlew jibImageTar
```

Tar file should be available at ```build/quarkus-jib.tar```.

Build container image and write the image to local Docker repository.

On Windows:

```bat
gradlew jibDockerBuild
```

On Linux/Mac:

```bat
./gradlew jibDockerBuild
```

Image ```quarkus-jib``` should be installed to local Docker repository. 

List images (Linux/Mac/Windows):

```bash
docker image ls
```

Run image ```quarkus-jib``` (Linux/Mac/Windows):

```bash
docker run -ti --rm -p 8080:8080 quarkus-jib
```