package xyz.ronella.gradle.plugin.simple.git

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
     * The default directory to use if the default directory was not specified.
     */
    abstract Property<File> getDirectory()

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

    SimpleGitPluginExtension() {
        noop.convention(false)
        verbose.convention(false)
        repoType.convention('github')
    }

    def writeln(String text) {
        if (verbose.get() || noop.get()) {
            println(text)
        }
    }

}
