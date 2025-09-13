package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.provider.ListProperty

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

    @Override
    protected ListProperty<String> getAllArgs() {
        def newArgs = super.getAllArgs()

        if (zargs.isPresent()) {
            newArgs.addAll(zargs.get())
        }

        return newArgs
    }
}