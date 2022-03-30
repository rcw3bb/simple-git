package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
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
abstract class GitFetchPR extends GitTask {

    GitFetchPR() {
        super()
        description = 'A convenience git fetch command for targeting a pull request.'
        command.convention('fetch')
        forceDirectory.convention(true)
    }

    /**
     * The pull request ID to be fetched.
     * @return The pull request ID.
     */
    @Input
    abstract Property<Long> getPullRequest()

    /**
     * The remote from where to fetch the pull request ID.
     *
     * @return The name of the remote.
     */
    @Input
    abstract Property<String> getRemote()

    @Override
    def initFields() {
        super.initFields()
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git

        if (project.hasProperty('sg_remote')) {
            remote.convention((project.sg_remote as String).trim())
            pluginExt.writeln("Found sg_remote: ${remote}")
        }

        if (project.hasProperty('sg_pull_request')) {
            pullRequest.convention(Long.valueOf((project.sg_pull_request as String).trim()))
            pluginExt.writeln("Found sg_pull_request: ${pullRequest}")
        }
    }

    @Override
    ListProperty<String> getAllArgs() {
        def newArgs = super.getAllArgs()

        initFields()

        if (remote.isPresent()) {
            newArgs.add(GitExecutor.quoteString(remote.get(), osType))
        }
        else {
            throw new MissingRemoteException()
        }

        if (pullRequest.isPresent()) {
            def prValue = pullRequest.get()
            def prBranch="pr-${prValue}"
            newArgs.add("pull/${prValue}/head:${prBranch}")
            project.ext.sg_branch = prBranch
        }
        else {
            throw new MissingPullRequestException()
        }

        if (zargs.isPresent()) {
            newArgs.addAll(zargs.get())
        }

        return newArgs
    }

}
