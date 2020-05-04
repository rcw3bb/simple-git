package xyz.ronella.gradle.plugin.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.exception.MissingPullRequestException
import xyz.ronella.gradle.plugin.exception.MissingRemoteException

class GitFetchPR extends GitTask {

    private String remote
    private long pullRequest

    public GitFetchPR() {
        super()
        command = 'fetch'
        forceDirectory = true
    }

    @Optional @Input
    long getPullRequest() {
        return pullRequest
    }

    void setPullRequest(long pullRequest) {
        this.pullRequest = pullRequest
    }

    @Optional @Input
    String getRemote() {
        return remote
    }

    void setRemote(String remote) {
        this.remote = remote
    }

    @Override
    public def initFields() {
        super.initFields()
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git;

        if (project.hasProperty('sg_remote')) {
            remote = (project.sg_remote as String).trim()
            pluginExt.writeln("Found sg_remote: ${remote}")
        }

        if (project.hasProperty('sg_pull_request')) {
            pullRequest = Long.valueOf((project.sg_pull_request as String).trim())
            pluginExt.writeln("Found sg_pull_request: ${pullRequest}")
        }
    }

    @Override
    public String[] getAllArgs() {
        String[] newArgs = super.getAllArgs()

        initFields()

        if (remote) {
            newArgs += "\"${remote}\""
        }
        else {
            throw new MissingRemoteException()
        }

        if (pullRequest) {
            def prBranch="pr-${pullRequest}"
            newArgs += "pull/${pullRequest}/head:${prBranch}"
            project.ext.sg_branch = prBranch
        }
        else {
            throw new MissingPullRequestException()
        }

        return newArgs
    }

}
