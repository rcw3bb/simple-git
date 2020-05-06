package xyz.ronella.gradle.plugin.simple.git

/**
 * The extension for customized the behaviour of the plugin for testing.
 *
 * @author Ron Webb
 * @since 2020-05-06
 */
class SimpleGitPluginTestExtension {

    /**
     * Test if no git is installed.
     */
    public boolean no_git_installed

    /**
     * Holds any test message.
     */
    public String test_message

}
