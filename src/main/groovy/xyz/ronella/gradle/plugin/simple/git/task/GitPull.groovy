package xyz.ronella.gradle.plugin.simple.git.task

/**
 * A convenience git task for pull command.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class GitPull extends GitTask {

    GitPull() {
        super()
        description = 'A convenience git pull command.'
        command.convention('pull')
        forceDirectory.convention(true)
    }
}
