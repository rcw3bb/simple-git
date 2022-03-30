package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.exception.MissingBranchException

/**
 * A convenience git task for branch command.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class GitBranch extends GitTask {

    @Input
    abstract Property<String> getBranch()

    GitBranch() {
        super()
        description = 'A convenience git branch command.'
        command.convention('branch')
        forceDirectory.convention(true)
    }

    @Override
    def initialization() {
        super.initialization()

        if (project.hasProperty('sg_branch')) {
            branch.convention((project.sg_branch as String).trim())
            EXTENSION.writeln("Found sg_branch: ${branch}")
        }
    }

    @Override
    ListProperty<String> getAllArgs() {
        def newArgs = super.getAllArgs()

        if (branch.isPresent()) {
            newArgs.add(GitExecutor.quoteString(branch.get(), OS_TYPE))
        }

        if (newArgs.isPresent() && newArgs.get().isEmpty()) {
            throw new MissingBranchException()
        }

        if (zargs.isPresent()) {
            newArgs.addAll(zargs.get())
        }

        return newArgs
    }

}
