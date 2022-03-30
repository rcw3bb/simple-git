package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.simple.git.exception.MissingBranchException

/**
 * A convenience git task for branch command for deletion.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class GitDeleteBranch extends GitBranch {

    @Optional @Input
    abstract Property<Boolean> getForce()

    GitDeleteBranch() {
        super()
        description = 'A convenience git branch command for deletion.'
    }

    @Override
    def initFields() {
        super.initFields()
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git

        if (project.hasProperty('sg_force')) {
            force.convention(Boolean.valueOf((project.sg_force as String).trim()))
            pluginExt.writeln("Found sg_force: ${force}")
        }
    }

    @Override
    ListProperty<String> getAllArgs() {
        def newArgs = super.getAllArgs()

        initFields()

        if (branch.isPresent()) {
            def quotedBranch= GitExecutor.quoteString(branch.get(), osType)
            def argsToClean = new ArrayList<String>(newArgs.get())

            argsToClean.removeIf({___arg -> quotedBranch==___arg || zargs.get().contains(___arg)})
            newArgs = project.getObjects().listProperty(String.class)
            newArgs.addAll(argsToClean.asList())
            newArgs.addAll((force.getOrElse(false) ? '-D' : '-d'), quotedBranch)
        }
        else {
            throw new MissingBranchException()
        }

        if (zargs.isPresent()) {
            newArgs.addAll(zargs.get())
        }

        return newArgs
    }
}
