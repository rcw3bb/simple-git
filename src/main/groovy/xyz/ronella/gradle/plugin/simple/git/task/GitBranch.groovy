package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.simple.git.exception.MissingBranchException

/**
 * A convenience git task for branch command.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
class GitBranch extends GitTask {

    private String branch

    public GitBranch() {
        super()
        command = 'branch'
        forceDirectory = true
    }

    /**
     * The name of the branch to act on.
     *
     * @return A branch name.
     */
    @Optional @Input
    String getBranch() {
        return branch
    }

    /**
     * Captures the branch name.
     *
     * @param branch The branch name.
     */
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
            newArgs += GitExecutor.quoteString(branch, osType)
        }

        if (!newArgs) {
            throw new MissingBranchException()
        }

        return newArgs
    }

}
