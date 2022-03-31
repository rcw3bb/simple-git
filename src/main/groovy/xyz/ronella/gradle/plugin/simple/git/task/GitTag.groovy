package xyz.ronella.gradle.plugin.simple.git.task

/**
 * A convenience git task for tag command.
 *
 * @author Ron Webb
 * @since 2022-03-31
 */
abstract class GitTag extends GitTask {
    GitTag() {
        super()
        description = 'A convenience git tag command.'
        command.convention('tag')
        forceDirectory.convention(true)
    }
}