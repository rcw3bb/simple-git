package xyz.ronella.gradle.plugin.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.exception.MissingBranchException

class GitCheckout extends GitTask {

    private String branch

    public GitCheckout() {
        super()
        command = 'checkout'
        forceDirectory = true
    }

    @Optional @Input
    String getBranch() {
        return branch
    }

    void setBranch(String branch) {
        this.branch = branch
    }

    @Override
    public def initFields() {
        super.initFields()
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git;

        if (project.hasProperty('sg_branch')) {
            branch = (project.sg_branch).trim()
            pluginExt.writeln("Found sg_branch: ${branch}")
        }
    }

    @Override
    public String[] getAllArgs() {
        String[] newArgs = super.getAllArgs()

        initFields()

        if (branch) {
            newArgs += "\"${branch}\""
        }

        if (!newArgs) {
            throw new MissingBranchException()
        }

        return newArgs
    }

}
