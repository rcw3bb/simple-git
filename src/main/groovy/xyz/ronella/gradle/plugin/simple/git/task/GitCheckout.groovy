package xyz.ronella.gradle.plugin.simple.git.task

/**
 * A convenience git task for checkout command.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
class GitCheckout extends GitBranch {

    public GitCheckout() {
        super()
        command = 'checkout'
        forceDirectory = true
    }

}
