package xyz.ronella.gradle.plugin.simple.git.task

/**
 * A convenience git task for knowing the git version.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class GitVersion extends GitTask {
    GitVersion() {
        super()
        description = 'A convenience git --version command.'
        args.add('--version')
        forceDirectory.convention(false)
    }
}
