package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.simple.git.exception.MissingRepositoryException

class GitClone extends GitTask {

    private String branch
    private String repository

    public GitClone() {
        super()
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

    @Optional @Input
    String getBranch() {
        return branch
    }

    void setBranch(String branch) {
        this.branch = branch
    }

    @Optional @Input
    String getRepository() {
        return repository
    }

    void setRepository(String repository) {
        this.repository = repository
    }

    @Override
    public String[] getAllArgs() {
        String[] newArgs = super.getAllArgs()

        initFields()

        if (branch) {
            newArgs += '--branch'
            newArgs += "\"${branch}\""
        }

        if (repository) {
            newArgs += "\"${repository}\""
        }
        else {
            throw new MissingRepositoryException()
        }

        newArgs += "\"${directory.absoluteFile.toString()}\""

        return newArgs
    }
}
