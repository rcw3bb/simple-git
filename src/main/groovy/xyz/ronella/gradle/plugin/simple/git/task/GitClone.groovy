package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.simple.git.exception.MissingRepositoryException

/**
 * A convenience git task for branch clone.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
class GitClone extends GitTask {

    private String branch
    private String repository

    public GitClone() {
        super()
        description = 'A convenience git clone command.'
        command = 'clone'
        forceDirectory = false
    }

    @Override
    public def initFields() {
        super.initFields()
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git;

        if (project.hasProperty('sg_repository')) {
            repository = (project.sg_repository as String).trim()
            pluginExt.writeln("Found sg_repository: ${repository}")
        }
        if (project.hasProperty('sg_branch')) {
            branch = (project.sg_branch).trim()
            pluginExt.writeln("Found sg_branch: ${branch}")
        }
    }

    /**
     * The branch to clone.
     *
     * @return The branch name.
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

    /**
     * The repository to clone.
     *
     * @return The repository address.
     */
    @Optional @Input
    String getRepository() {
        return repository
    }

    /**
     * Captures the repository to clone.
     * @param repository The repository address.
     */
    void setRepository(String repository) {
        this.repository = repository
    }

    @Override
    public String[] getAllArgs() {
        String[] newArgs = super.getAllArgs()

        initFields()

        if (branch) {
            newArgs += '--branch'
            newArgs += GitExecutor.quoteString(branch, osType)
        }

        if (repository) {
            newArgs += GitExecutor.quoteString(repository, osType)
        }
        else {
            throw new MissingRepositoryException()
        }

        newArgs += GitExecutor.quoteString(directory.absoluteFile.toString(), osType)

        return newArgs
    }
}
