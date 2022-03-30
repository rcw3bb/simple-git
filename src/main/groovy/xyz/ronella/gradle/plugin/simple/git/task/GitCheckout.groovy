package xyz.ronella.gradle.plugin.simple.git.task

/**
 * A convenience git task for checkout command.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class GitCheckout extends GitBranch {

    GitCheckout() {
        super()
        description = 'A convenience git checkout command.'
        command.convention('checkout')
        forceDirectory.convention(true)
    }

}
