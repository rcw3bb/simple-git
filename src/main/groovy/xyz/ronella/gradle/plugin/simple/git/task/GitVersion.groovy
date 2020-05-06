package xyz.ronella.gradle.plugin.simple.git.task

/**
 * A convenience git task for knowing the git version.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
class GitVersion extends GitTask {
    public GitVersion() {
        super()
        description = 'A convenience git --version command.'
        args = ['--version']
        forceDirectory = false
    }
}
