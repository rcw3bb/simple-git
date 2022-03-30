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
     * It is like verbose but not execute the git command.
     */
    abstract Property<Boolean> getNoop()

    /**
     * The default directory to use if the default directory was not specified.
     */
    abstract Property<File> getDirectory()

    SimpleGitPluginExtension() {
        noop.convention(false)
        verbose.convention(false)
    }

    def writeln(String text) {
        if (verbose.get() || noop.get()) {
            println(text)
        }
    }

}
