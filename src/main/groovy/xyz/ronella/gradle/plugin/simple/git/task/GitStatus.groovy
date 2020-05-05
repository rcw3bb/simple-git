package xyz.ronella.gradle.plugin.simple.git.task

/**
 * A convenience git task for status command.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
class GitStatus extends GitTask {

    public GitStatus() {
        super()
        command = 'status'
        forceDirectory = true
    }
}
