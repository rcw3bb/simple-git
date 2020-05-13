package xyz.ronella.gradle.plugin.simple.git.task

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
class GitDeleteBranch extends GitBranch {

    private boolean force

    public GitDeleteBranch() {
        super()
        description = 'A convenience git branch command for deletion.'
    }

    /**
     * Indicates force deletion of the branch if true.
     *
     * @return Returns true of force deletion of the branch.
     */
    @Optional @Input
    boolean getForce() {
        return force
    }

    /**
     * Captures if force deletion must be performed.
     * @param force Set to true to force branch deletion locally.
     */
    void setForce(boolean force) {
        this.force = force
    }

    @Override
    public def initFields() {
        super.initFields()
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git;

        if (project.hasProperty('sg_force')) {
            force = Boolean.valueOf((project.sg_force as String).trim())
            pluginExt.writeln("Found sg_force: ${force}")
        }
    }

    @Override
    public String[] getAllArgs() {
        String[] newArgs = super.getAllArgs()

        initFields()

        if (branch) {
            def quotedBranch= GitExecutor.quoteString(branch, osType)
            def argsToClean = newArgs.toList()

            argsToClean.removeIf({___arg -> quotedBranch==___arg || zargs.contains(___arg)})
            newArgs = argsToClean.toArray()
            newArgs += [(force ? '-D' : '-d'), quotedBranch]
        }
        else {
            throw new MissingBranchException()
        }

        newArgs += zargs

        return newArgs
    }
}
