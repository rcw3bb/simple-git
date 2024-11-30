package xyz.ronella.gradle.plugin.simple.git

import org.gradle.api.logging.Logger
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

/**
 * The extension for customized the behaviour of the plugin.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class SimpleGitPluginExtension {

    /**
     * Displays more information.
     */
    abstract Property<Boolean> getVerbose()

    /**
     * It is like verbose but don't execute the git command.
     */
    abstract Property<Boolean> getNoop()

    /**
     * The default directory to use if the directory was not specified.
     */
    abstract Property<File> getDirectory()

    /**
     * The default branch to use if the branch was not specified.
     */
    abstract Property<String> getBranch()

    /**
     * The default remote to use if the remote was not specified.
     */
    abstract Property<String> getRemote()

    /**
     * The repository type that controls how the command parameters are processed.
     * Possible value could be github and bitbucket. The default value is github.
     */
    abstract Property<String> getRepoType()

    /**
     * The pull request to pattern to use if default repo type doesn't support it.
     *
     * Possible value is 'pull/%s/head:%s'
     *
     * Where the first %s will be replaced with the actual PR code and the second %s with the branch.
     */
    abstract Property<String> getPullRequestPattern()

    /**
     * The default username for the git command.
     */
    abstract Property<String> getUsername()

    /**
     * The default password for the git command.
     */
    abstract Property<String> getPassword()

    /**
     * The default options to all the tasks.
     */
    abstract ListProperty<String> getDefaultOptions()

    /**
     * The default args to all the tasks.
     */
    abstract ListProperty<String> getDefaultArgs()

    /**
     * The default arguments to all the clone tasks.
     */
    abstract ListProperty<String> getDefaultCloneArgs()

    /**
     * The default options to all the clone tasks.
     */
    abstract ListProperty<String> getDefaultCloneOptions()

    /**
     * The default arguments to all the branch tasks.
     */
    abstract ListProperty<String> getDefaultBranchArgs()

    /**
     * The default options to all the branch tasks.
     */
    abstract ListProperty<String> getDefaultBranchOptions()

    /**
     * The default options to all the pull tasks.
     */
    abstract ListProperty<String> getDefaultPullOptions()

    /**
     * The default arguments to all the pull tasks.
     */
    abstract ListProperty<String> getDefaultPullArgs()

    abstract Property<Logger> getLogger()

    SimpleGitPluginExtension() {
        noop.convention(false)
        verbose.convention(false)
        repoType.convention('github')
        branch.convention('master')
        remote.convention('origin')
    }

    def writeln(String text) {
        if (verbose.get() || noop.get()) {
            logger.get().lifecycle(text)
        }
    }

}
