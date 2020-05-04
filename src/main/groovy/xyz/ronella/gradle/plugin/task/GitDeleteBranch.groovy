package xyz.ronella.gradle.plugin.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.exception.MissingBranchException

class GitDeleteBranch extends GitBranch {

    private boolean force

    public GitDeleteBranch() {
        super()
    }

    @Optional @Input
    boolean getForce() {
        return force
    }

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
            def quotedBranch="\"${branch}\""
            def argsToClean = newArgs.toList()

            argsToClean.removeIf({___arg -> quotedBranch==___arg})
            newArgs = argsToClean.toArray()
            newArgs += [(force ? '-D' : '-d'), quotedBranch]
        }
        else {
            throw new MissingBranchException()
        }

        return newArgs
    }
}
