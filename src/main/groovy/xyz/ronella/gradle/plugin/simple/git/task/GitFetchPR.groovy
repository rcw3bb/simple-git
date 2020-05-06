package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.simple.git.exception.MissingPullRequestException
import xyz.ronella.gradle.plugin.simple.git.exception.MissingRemoteException

/**
 * A convenience git task for fetching a pull request.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
class GitFetchPR extends GitTask {

    private String remote
    private long pullRequest

    public GitFetchPR() {
        super()
        description = 'A convenience git fetch command for targeting a pull request.'
        command = 'fetch'
        forceDirectory = true
    }

    /**
     * The pull request ID to be fetched.
     * @return The pull request ID.
     */
    @Optional @Input
    long getPullRequest() {
        return pullRequest
    }

    /**
     * Captures the pull request id to be fetched.
     * @param pullRequest The pull request ID.
     */
    void setPullRequest(long pullRequest) {
        this.pullRequest = pullRequest
    }

    /**
     * The remote from where to fetch the pull request ID.
     *
     * @return The name of the remote.
     */
    @Optional @Input
    String getRemote() {
        return remote
    }

    /**
     * Captures the remote from where to fetch the pull request ID
     * @param remote The name of the remote.
     */
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
            newArgs += GitExecutor.quoteString(remote, osType)
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
