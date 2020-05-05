package xyz.ronella.gradle.plugin.simple.git

/**
 * The extension for customized the behaviour of the plugin.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
class SimpleGitPluginExtension {

    /**
     * Displays more information.
     */
    public boolean verbose

    /**
     * It is like verbose but not execute the git command.
     */
    public boolean noop

    /**
     * The default directory to use if the default directory was not specified.
     */
    public File directory

    /**
     * A convenience method for writing more information on the console.
     *
     * @param text The text to write on console.
     */
    public void writeln(String text) {
        if (verbose || noop) {
            println(text)
        }
    }

}
