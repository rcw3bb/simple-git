# Build

## Pre-requisite

* Java 8

* Create or update **<USER_HOME>\\.gradle\\gradle.properties** to have the following properties:

    ```properties
    artifactoryUsername=<VALID_USERNAME>
    artifactoryPassword=<VALID_PASSWORD>
    ```
    
    > If you don't have access to my **artifactory**, update all the **repositories section** in the **build.gradle** file **after cloning**, from:
    >
    > ```
    > repositories {
    >        maven {
    >            url 'https://repo.ronella.xyz/artifactory/java-central'
    >            credentials {
    >                username "${artifactoryUsername}"
    >                password "${artifactoryPassword}"
    >            }
    >        }
    >    }
    >    ```
    >    
    >    to
    >    
    >    ```
    >    repositories {
    >  mavenCentral()
    > }
    >```

## Running Unit Test

Run the following command to where you've cloned this repository:

```
gradlew clean check
```

## Build

Run the following command to where you've cloned this repository:

```
gradlew jar
```

> The **generated jar** file will be in the **libs directory** inside the **build directory** from where you've cloned the this repository.

