# Simple Git Gradle Plugin

The plugin that allows you access to git commands in gradle as task.

# Pre-requisite

* Java 11 (Minimum)
* Windows/Linux/MacOS
* Git Application

## Plugging in the simple-git

In your **build.gradle** file add the following plugin:

```groovy
plugins {
    id "xyz.ronella.simple-git" version "2.0.0"
}
```

> A **Simple Git tasks** group will be added to the available tasks at your disposal. You can use the following command to see them:
>
> ```
> gradlew tasks --group "Simple Git"
> ```
>
> Expect to see the available tasks like the following:
>
> ```
> Simple Git tasks
> ----------------
> gitBranch - A convenience git branch command.
> gitCheckout - A convenience git checkout command.
> gitClone - A convenience git clone command.
> gitDeleteBranch - A convenience git branch command for deletion.
> gitFetchPR - A convenience git fetch command for targeting a pull request.
> gitPull - A convenience git pull command.
> gitStatus - A convenience git status command.
> gitTag - A convenience git tag command.
> gitTask - Execute a git command.
> gitVersion - A convenience git --version command.
> ```

## GIT_HOME Environment Variable

The first location that the plugin will try to look for the **git executable** will be the location set by **GIT_HOME** environment variable. If the plugin cannot detect the location of the installed **git application**, it is advisable to set this variable to the correct directory where the git executable lives.

## Plugin Properties

| Property | Description | Type | Default |
|-----|------|------|-----|
| simple_git.branch | The default branch to use by the convenience tasks except for gitDeleteBranch task. You don't want to accidentally delete a branch. | String | master |
| simple_git.directory | Tells the plugin what is the default git application directory it will work on. | File | *The project directory* |
| simple_git.noop | This is like the verbose property with the addition of not running the git command. This is good for debugging on what command parameters it is trying to execute. | boolean | false |
| simple_git.pullRequestPattern | Explicitly define the pull request pattern overriding the effect of repoType parameter. <br/><br/>Example value is **pull/%s/head:%s**<br/><br/>Where the first %s will be replaced with the actual PR code and the second %s with the calculated branch *(i.e. normally with the syntax **pr-<PR-CODE>**)*. | String |  |
| simple_git.remote | The default remote to use by the convenience tasks. | String | origin |
| simple_git.repoType | The repository type that controls how the command parameters are processed. The valid possible values are **github** or **bitbucket**. | String | github |
| simple_git.verbose | The plugin will to display more information on the console *(e.g. the actual git command being run)*. | boolean | false |

## The forceDirectory task property

The git command is normally performed inside a **git project directory** *(e.g. git status)*. Hence, there's no need to specify the directory. The **forceDirectory**, will ensure that the **git command** will be performed inside a git project directory *(i.e. directory with **.git** directory)*. Since most of the git command requires that it must be executed in this directory, **the forceDirectory is defaulted to true**.  However, it will only take effect if the **directory property is not null**.

## General Syntax

```
<GIT_EXECUTABLE> <GIT_OPTIONS> <GIT_COMMAND> <GIT_ARGUMENTS>
```

| Token | Description | Task Property | Gradle Command Line Argument | Type |
|------|------|------|--------|------|
| GIT_EXECUTABLE | The detected git executable by the plugin. |  |  | |
| GIT_OPTIONS | The options for running the command. This is normally use for setting inline configuration before running the git command. | options | sg_options | String[] |
| GIT_COMMAND | The git command to be executed | command | sg_command | String |
| GIT_ARGUMENTS | The arguments for the git command. | args | sg_args | String[] |

> All these task properties *(i.e. options, command and args)* are always available to all the tasks *(i.e. including the convience tasks)*.
>
> The **String[]** in the command line will be all the values **delimited by comma** assigned to an argument *(e.g. -P**sg_args**=**-D,pr-2**)*

#### Example

```
git -c diff.mnemonicprefix=false -c core.quotepath=false --no-optional-locks branch -D pr-2
```

| Token          | Value                                                        |
| -------------- | ------------------------------------------------------------ |
| GIT_EXECUTABLE | git                                                          |
| GIT_OPTIONS    | -c diff.mnemonicprefix=false -c core.quotepath=false --no-optional-locks |
| GIT_COMMAND    | branch                                                       |
| GIT_ARGUMENTS  | -D pr-2                                                      |

## Using gitTask

All the member tasks of **Simple Git** group is a child for **gitTask**. The **child task** normally just have a default command and/or arguments *(e.g. **gitStatus** task has **status as the command**)*. 

Whatever you can do with the **git command** in console you can do it in gradle with this task. 

| Task Name | Task Property | Gradle Command Line Argument | Type     |
| --------- | ------------- | ---------------------------- | -------- |
| gitTask   | args          | sg_args                      | String[] |
|           | command       | sg_command                   | String   |
|           | directory     | sg_directory                 | String   |
|           | options       | sg_options                   | String[] |

#### Example

Translate the following **git clone command** into a task in gradle:

```
git clone https://github.com/rcw3bb/simple-git.git C:\tmp\simple-git
```

**Use the task itself using the following:**

```groovy
gitTask {
  forceDirectory = false//Just directly use the git executable. 
  command = 'clone' //Git Command
  args = ['https://github.com/rcw3bb/simple-git.git', 'C:\\tmp\\simple-git'] //The git command arguments
}
```

**Use the child task gitClone with the following:**

```groovy
gitClone {
  repository = 'https://github.com/rcw3bb/simple-git.git'
  directory = new File('C:\\tmp\\simple-git')
}
```

> You don't need to set the **command property** because it was already preset with **clone**.

**Create your own task of type GitTask like the following:**

```groovy
task cloneSimpleGitByGitTask(type: GitTask) {
  forceDirectory = false//Just directly use the git executable. 
  command = 'clone' //Git Command
  args = ['https://github.com/rcw3bb/simple-git.git', 'C:\\tmp\\simple-git'] //The git command arguments
}
```

> To use **GitTask class** as the type of your task, you must add the following at the top of your **build.gradle** file:
>
> ```groovy
> import xyz.ronella.gradle.plugin.simple.git.task.*
> ```
>
> Note: Each **default simple git tasks** has equivalent class file. The class file has the prefix **Git** instead of **git** of the normal gradle task *(e.g. **gitClone** gradle task has an equivalent class of **GitClone**)*.

**Create your own task of type GitClone for convenience like the following:**

``` groovy
task cloneSimpleGit(type: GitClone ) {
  repository = 'https://github.com/rcw3bb/simple-git.git'
  directory = new File('C:\\tmp\\simple-git')
}
```

> You don't need to set the **command property** because it was already preset with **clone**.
>

## Sample build.gradle File

``` groovy
plugins {
  id "xyz.ronella.simple-git" version "2.0.0"
}

simple_git.directory=new File('C:\\tmp\\simple-git')

gitClone {
  repository = 'https://github.com/rcw3bb/simple-git.git'
}
```
## Convenience Tasks and Their Task Properties

| Task Name       | Task Property | Gradle Command Line Argument *(i.e. always of type String)* | Task Type | Support for zargs *(i.e. sg_zargs in command line)* Terminal Arguments |
| --------------- | ------------- | ------ | ------- | ------- |
| gitBranch       | branch        | sg_branch |String  |true  |
|        | directory        | sg_directory |File  |  |
| gitCheckout     | branch | sg_branch |String|true|
|  | directory | sg_directory |File||
| gitClone        | branch        | sg_branch |String  |true  |
|                 | directory    | sg_directory |File  |  |
|                 | repository    | sg_repository |String  |  |
| gitDeleteBranch | branch        | sg_branch |String  |true  |
|                 | directory         | sg_directory | File |  |
|                 | force         | sg_force | boolean |  |
| gitFetchPR     | directory | sg_directory |File  |true  |
|      | remote        | sg_remote |String  |  |
|                 | pullRequest   | sg_pull_request |long    |    |
| gitPull         | directory | sg_directory | File |  |
| gitStatus      | directory | sg_directory | File |  |
| gitTag | directory | sg_directory | File | true |
| gitVersion |  |  |  |  |

> The **options** and **args** tasks properties are always available.
>
> The **directory** property must be a valid **git application directory** except for the **gitClone task**.

## directoryIsEmpty Method

The **directoryIsEmpty() is a convenience method** that can be used in **onlyIf block** like the following:

```groovy
gitClone {
  onlyIf {
    directoryIsEmpty()
  }
  directory = file('C:\\tmp\\simple-git\\')
  repository='https://github.com/rcw3bb/simple-git.git'
}
```

## Terminal Arguments (zargs)

Some convenience tasks supports terminal arguments *(i.e. zargs)*. This means it can accepts additional arguments after their **task provided properties *(i.e. normally becomes the last arguments)***. For example for the **gitCheckout task** to also have **track argument**, will be like the following: 

```groovy
gitCheckout {
  directory = new File('C:\\tmp\\simple-git')
  args = ['-B']
  branch = 'master'
  zargs = ['--track', 'origin/master']
}
```

## Using the Git Task in Gradle Command Line

All the available tasks in simple git can be run with gradle command like the following:

```
gradle gitClone -Psg_options='-c,core.longpaths=true' -Psg_repository=https://github.com/rcw3bb/simple-git.git -Psg_directory=C:\tmp\simple-git
```

> The preceding command includes a sample usage of **sg_options**. For this particular case it includes the support **long paths**. The actual git command is like the following:
>
> ```
> git -c core.longpaths=true clone https://github.com/rcw3bb/simple-git.git C:\tmp\simple-git
> ```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## [Build](BUILD.md)

## [Changelog](CHANGELOG.md)

## Author

* Ronaldo Webb
