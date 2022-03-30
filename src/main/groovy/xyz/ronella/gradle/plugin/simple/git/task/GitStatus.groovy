package xyz.ronella.gradle.plugin.simple.git.task

/**
 * A convenience git task for status command.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class GitStatus extends GitTask {

    GitStatus() {
        super()
        description = 'A convenience git status command.'
        command.convention('status')
        forceDirectory.convention(true)
    }
}
