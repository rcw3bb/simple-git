package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.exception.MissingBranchException

/**
 * A convenience git task for branch command.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class GitBranch extends GitTask {

    @Optional @Input
    abstract Property<String> getBranch()

    GitBranch() {
        super()
        description = 'A convenience git branch command.'
        command.convention('branch')
        forceDirectory.convention(true)
        internalZArgs.convention(EXTENSION.getDefaultBranchArgs().orElse([]))
        internalOptions.convention(EXTENSION.getDefaultBranchOptions().orElse([]))        
    }

    @Override
    protected void initialization() {
        super.initialization()

        if (project.hasProperty('sg_branch')) {
            branch.convention((project.sg_branch as String).trim())
            logger.lifecycle("Found sg_branch: ${branch}")
        }
    }

    @Override
    ListProperty<String> getAllArgs() {
        def newArgs = super.getAllArgs()

        def targetBranch = java.util.Optional.ofNullable(branch.getOrElse(EXTENSION.branch.getOrNull()))

        targetBranch.ifPresent {___branch ->
            newArgs.add(GitExecutor.quoteString(___branch, OS_TYPE))
        }

        if (targetBranch.isEmpty()) {
            throw new MissingBranchException()
        }

        if (zargs.isPresent()) {
            newArgs.addAll(zargs.get())
        }

        return newArgs
    }

}
