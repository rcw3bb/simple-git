package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.exception.MissingPullRequestException
import xyz.ronella.gradle.plugin.simple.git.exception.MissingRemoteException

/**
 * A convenience git task for fetching a pull request.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class GitFetchPR extends GitTask {

    /**
     * The collection of pull request pattern per repository.
     *
     * @author Ron Webb
     * @since 2022-03-31
     */
    enum EnumRepoTypePattern {
        GITHUB("pull/%s/head:%s"),
        BITBUCKET("pull-requests/%s/from:%s")

        private String pattern

        private EnumRepoTypePattern(String pattern) {
            this.pattern = pattern
        }

        String getPattern(Long prCode, String branch) {
            return String.format(pattern, prCode.toString(), branch)
        }

        static EnumRepoTypePattern of(String repoType) {
            def enumFound = Optional.ofNullable(values().find { (it.name() == repoType.toUpperCase()) })
            return enumFound.orElse(GITHUB)
        }
    }

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
    abstract Property<Integer> getPullRequest()

    /**
     * The remote from where to fetch the pull request ID.
     *
     * @return The name of the remote.
     */
    @Input
    abstract Property<String> getRemote()

    @Override
    def initialization() {
        super.initialization()

        if (project.hasProperty('sg_remote')) {
            remote.convention((project.sg_remote as String).trim())
            EXTENSION.writeln("Found sg_remote: ${remote}")
        }

        if (project.hasProperty('sg_pull_request')) {
            pullRequest.convention(Integer.valueOf((project.sg_pull_request as String).trim()))
            EXTENSION.writeln("Found sg_pull_request: ${pullRequest}")
        }
    }

    @Override
    ListProperty<String> getAllArgs() {
        def newArgs = super.getAllArgs()

        if (remote.isPresent()) {
            newArgs.add(GitExecutor.quoteString(remote.get(), OS_TYPE))
        }
        else {
            throw new MissingRemoteException()
        }

        if (pullRequest.isPresent()) {
            def prValue = pullRequest.get()
            def prBranch="pr-${prValue}"
            def repoType = EnumRepoTypePattern.of(EXTENSION.repoType.get())
            def prArg = EXTENSION.pullRequestPattern.isPresent() ?
                    String.format(EXTENSION.pullRequestPattern.get(), prValue, prBranch)
                    : repoType.getPattern(prValue, prBranch)
            newArgs.add(prArg)
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
