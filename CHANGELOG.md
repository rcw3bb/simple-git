# Changelog

## 2.2.1 : 2024-12-09

### Minor Change

* Make the shadow package part of the plugin package.

## 2.2.0 : 2024-11-30

### New

* Throw exception when no git executable to use.

## 2.1.0 : 2024-11-30

### New

* Username and password can now be set at the extension level or task level.
* Ability to set default arguments at the extension level.

## 2.0.3 : 2023-04-21

### Fix

* Use the first detected executable.

## 2.0.2 : 2023-04-21 (DO NOT USE THIS IS BROKEN)

## 2.0.1 : 2022-04-01

### Fix

* Resolve the NPE when running gradle in java 17.

## 2.0.0 : 2022-04-01

### New

* Refactored to use **lazy configurations**.
* Introduced the default **branch** and **remote**.
* Introduced the **repoType** parameter to specify the type of repository.
* Introduced the **pullRequestPattern** to specify the pull request pattern.
* **gitTag** convenience task that represents git tag command.
* **directoryIsEmpty** convenience method.

## 1.3.0 : 2020-05-14

### New

* Add MacOS support.

## 1.2.0 : 2020-05-13

### New

* Introduce terminal arguments via **zargs**.

## 1.1.0 : 2020-05-06

### New

* gitVersion convenience task to show the git version used.

## 1.0.0 : 2020-05-06

### Initial Version

