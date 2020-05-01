package xyz.ronella.gradle.plugin.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.exception.MissingRepositoryException

class GitClone extends GitTask {

    private String branch
    private String repository

    public GitClone() {
        super()
        command = 'clone'
        pushDirectory = false
    }

    @Override
    public def initFields() {
        super.initFields()
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git;

        if (project.hasProperty('repository')) {
            repository = project.repository
            pluginExt.writeln("Found repository: ${repository}")
        }
        if (project.hasProperty('branch')) {
            branch = project.branch
            pluginExt.writeln("Found branch: ${branch}")
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
