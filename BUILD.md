# Build

## Pre-requisite

* Java 21

## Running Unit Test

Run the following command to where you've cloned this repository:

```
gradlew clean check
```

## Building

Run the following command to where you've cloned this repository:

```
gradlew jar
```

> The **generated jar** file will be in the **libs directory** inside the **build directory** from where you've cloned the this repository.

## Publishing

Run the following command:

```
gradlew publishPlugins
```

This will be published to the following address:

https://plugins.gradle.org/plugin/xyz.ronella.simple-git
