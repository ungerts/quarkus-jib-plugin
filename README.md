# quarkus-jib-plugin

![](https://github.com/ungerts/quarkus-jib-plugin/workflows/build/badge.svg)

## Configuration

Plugin can be configured in ```build.gradle```.

```groovy

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath "de.ungerts:quarkus-jib-plugin:1.0-SNAPSHOT"
    }
}

apply plugin: 'de.ungerts.quarkus-jib'

quarkusJib {
    imageName = "${project.name}"
}
```

Parameters:

Parameter | Default | Description 
--- | --- | --- 
`libsDirPath` | `"${project.buildDir}${File.separator}lib"` | 
`applicationLayersCachePath` | `"${project.buildDir}${File.separator}jib-app-cache"` | 
`baseImageLayersCachePath` | `"${project.buildDir}${File.separator}jib-base-cache"` | 
`baseImage` | `'gcr.io/distroless/java:11'` | 
`imageName` | `'runner-image'` | 
`exposedPort` | `8080` | 
`offlineMode` | `false` | 

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

Image ```quarkus-jib ``` should be installed to local Docker repository. 

List images (Linux/Mac/Windows):

```bash
docker image ls
```